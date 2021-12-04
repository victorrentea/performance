package victor.training.performance.primitives.probes;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MonitoringSystem {
   public static final boolean PLOTTER_ACCEPTS_ONLY_PAGES = true;
   private final Probes probes;
   private final Plotter plotter;
   private final Deque<Sample> pendingSamples = new ArrayDeque<>();
   //   private final ExecutorService sendPool = Executors.newFixedThreadPool(1);
   private final ExecutorService sendPool = new ThreadPoolExecutor(1, 1,
       1, TimeUnit.SECONDS,
       new ArrayBlockingQueue<>(40),
       new DiscardOldestPolicy());
   private boolean printing;

   public MonitoringSystem(Probes probes, Plotter plotter) {
      this.probes = probes;
      this.plotter = plotter;
      probes.setReceiveFunction(this::receive);
   }

   public static void main(String[] args) {
      new MonitoringSystem(new Probes(), new Plotter(400)).start();
   }

   public void start() {
      log.debug("START");
      probes.requestMetricFromProbe("probe1");
      probes.requestMetricFromProbe("probe2");
      probes.requestMetricFromProbe("probe3");
   }

   // is called on multiple threads by Probes driver
   public void receive(String device, int value) {

      synchronized (pendingSamples) {
         Sample sample = new Sample(LocalTime.now(), device, value);
         pendingSamples.offer(sample);
         while (pendingSamples.size() > 40) {
            pendingSamples.poll();
         }
         if (pendingSamples.size() >= 5 && !printing) {
            printing = true;
            List<Sample> page = pollMany(pendingSamples, 5);
            sendPool.submit(() -> submit(page));
         }
//      sendPool.submit(() -> sendToPlotter(List.of(sample)));

         probes.requestMetricFromProbe(device);
      }


   }

   private void submit(List<Sample> page) {
      plotter.sendToPlotter(page); // block for a long time
      synchronized (pendingSamples) {
         if (pendingSamples.size() >= 5) {
            List<Sample> newPage = pollMany(pendingSamples, 5);
            sendPool.submit(() -> submit(newPage));
         } else {
            printing = false;
         }
      }
   }

   private static List<Sample> pollMany(Deque<Sample> pendingSamples, int n) {
      List<Sample> page = new ArrayList<>();
      for (int i = 0; i < n; i++) {
         page.add(pendingSamples.poll());
      }
      return page;
   }
}
