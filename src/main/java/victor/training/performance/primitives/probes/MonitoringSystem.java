package victor.training.performance.primitives.probes;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

@Slf4j
public class MonitoringSystem {
   public static final boolean PLOTTER_ACCEPTS_ONLY_PAGES = true;

   public static void main(String[] args) {
      new MonitoringSystem(new Probes(), new Plotter(400)).start();
   }

   private final Probes probes;

   private final  Plotter plotter;

   public MonitoringSystem(Probes probes, Plotter plotter) {
      this.probes = probes;
      this.plotter = plotter;
      probes.setReceiveFunction(this::receive);
   }
   public void start() {
      log.debug("START");
      probes.requestMetricFromProbe("probe1");
      probes.requestMetricFromProbe("probe2");
      probes.requestMetricFromProbe("probe3");
   }

   public final List<Sample> currentPage = new ArrayList<>();


   public void receive(String device, int value) {

      synchronized (currentPage) {
         Sample sample = new Sample(LocalTime.now(), device, value);
         currentPage.add(sample);
         if (currentPage.size() == 5) {
            ArrayList<Sample> page = new ArrayList<>(currentPage);
            sendPool.submit(() -> plotter.sendToPlotter(page));
            currentPage.clear();
         }
      }
//      sendPool.submit(() -> sendToPlotter(List.of(sample)));

      probes.requestMetricFromProbe(device);
   }

   //   private final ExecutorService sendPool = Executors.newFixedThreadPool(1);
   private final ExecutorService sendPool = new ThreadPoolExecutor(1, 1,
       1, TimeUnit.SECONDS,
       new ArrayBlockingQueue<>(40),
       new DiscardOldestPolicy());



}
