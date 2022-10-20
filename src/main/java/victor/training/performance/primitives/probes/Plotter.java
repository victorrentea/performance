package victor.training.performance.primitives.probes;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.primitives.probes.MonitoringSystem.PLOTTER_ACCEPTS_ONLY_PAGES;

public class Plotter {
   private static final Logger log = LoggerFactory.getLogger(Plotter.class);
   private final List<String> callingThreads = new ArrayList<>();
   private final List<Integer> receivedValues = new ArrayList<>();
   private final int blockingMillis;

   public Plotter(int blockingMillis) {
      this.blockingMillis = blockingMillis;
   }

   public void sendToPlotter(List<Sample> samples) {
      log.debug("SEND " + samples + " to plotter ... ");
      checkReceivedPage(samples);
      synchronized (callingThreads) {
         callingThreads.add(Thread.currentThread().getName());
         if (callingThreads.size() >= 2) {
            System.err.println("PLOTTER ERROR: received more than 1 parallel requests, from threads: " + callingThreads);
            System.exit(1);
         }
      }
      checkSamplesInOrder(samples);
      PerformanceUtil.sleepMillis(blockingMillis);
      receivedValues.addAll(samples.stream().map(Sample::getValue).collect(toList()));
      log.debug("SEND done");
      synchronized (callingThreads) {
         callingThreads.remove(Thread.currentThread().getName());
      }
   }

   @VisibleForTesting
   public List<Integer> getReceivedValues() {
      return receivedValues;
   }

   private void checkReceivedPage(List<Sample> samples) {
      if (PLOTTER_ACCEPTS_ONLY_PAGES) {
         if (samples.size() != 5) {
            System.err.println("PLOTTER ERROR: received a page of size != 5: " + samples);
            System.exit(2);
         }
      }
   }
   private LocalTime lastSampleTime = LocalTime.now();
   private void checkSamplesInOrder(List<Sample> samples) {
      for (Sample sample : samples) {
         if (lastSampleTime.isAfter(sample.getTimestamp())) {
            System.err.println("PLOTTER ERROR: Samples received out of order.\nOffending timestamp:"+ sample.getTimestamp() + "\nis before\nlast timestamp:"+lastSampleTime);
            System.exit(3);
         }
         lastSampleTime = sample.getTimestamp();
      }
   }
}
