package victor.training.performance.concurrency.primitives.probes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Probes {
   private static final Logger log = LoggerFactory.getLogger(Probes.class);
   private static final ScheduledExecutorService replyPool = Executors.newScheduledThreadPool(100);
   private static final long startTime = currentTimeMillis();
   private BiConsumer<String, Integer> receiveFunction;

   public void setReceiveFunction(BiConsumer<String, Integer> receiveFunction) { // needed due to cyclic dependency
      this.receiveFunction = receiveFunction;
   }

   private final Deque<ValueAndDelay> responses;

   public Probes(List<ValueAndDelay> responses) {
      this.responses = new ArrayDeque<>(responses);
   }

   public Probes() {
      this(generateResponses(100));
   }

   private static List<ValueAndDelay> generateResponses(int samplesCount) {
      List<ValueAndDelay> list = new ArrayList<>();
      for (int i = 0; i < samplesCount; i++) {
         int delayMillis = PerformanceUtil.randomInt(1, 100);
//      int delayMillis = PerformanceUtil.randomInt(500, 500);
         int value = (int) ((currentTimeMillis() + delayMillis - startTime) / 10);
         list.add(new ValueAndDelay(value, delayMillis));
      }
      return list;
   }

   private final AtomicInteger sampleNo = new AtomicInteger();

   public void requestMetricFromProbe(String probe) {
      ValueAndDelay response = responses.poll();
      replyPool.schedule(() -> replyFromProbe(probe, response), response.delayMillis, MILLISECONDS);
   }

   private void replyFromProbe(String probe, ValueAndDelay response) {
      log.debug("{} sends sample #{}: {}", probe, sampleNo.incrementAndGet(), response.value);
      receiveFunction.accept(probe, response.value);
   }


   public static class ValueAndDelay {
      private final int value;
      private final int delayMillis;

      public ValueAndDelay(int value, int delayMillis) {
         this.value = value;
         this.delayMillis = delayMillis;
      }

      public int getDelayMillis() {
         return delayMillis;
      }

      public int getValue() {
         return value;
      }
   }

}
