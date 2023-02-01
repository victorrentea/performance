package victor.training.performance.primitives.probes;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import victor.training.performance.concurrency.primitives.probes.MonitoringSystem;
import victor.training.performance.concurrency.primitives.probes.Plotter;
import victor.training.performance.concurrency.primitives.probes.Probes;
import victor.training.performance.concurrency.primitives.probes.Probes.ValueAndDelay;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

@Slf4j
class MonitoringSystemTest {
   public static final int EARLY_VALUE = 0;
   public static final int LATE_VALUE = 1;

   @Test
   void discardsOldSamplesOnOverflow() {
      List<ValueAndDelay> responses = new ArrayList<>();
      for (int i = 0; i < 41; i++) responses.add(new ValueAndDelay(EARLY_VALUE, 5));
      for (int i = 0; i < 40; i++) responses.add(new ValueAndDelay(LATE_VALUE, 5));
      Plotter plotter = new Plotter(100);
      MonitoringSystem target = new MonitoringSystem(new Probes(responses), plotter);

      target.start();

      awaitUntilValueStabilizes(plotter::getReceivedValues, ofMillis(500));

      System.out.println(plotter.getReceivedValues());
      try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
         softly.assertThat(plotter.getReceivedValues().stream().filter(v1 -> v1 == EARLY_VALUE).count())
             .describedAs("Older values should have been discarded, proving eviction")
             .isLessThan(40);
         softly.assertThat(plotter.getReceivedValues().stream().filter(v -> v == LATE_VALUE).count())
             .describedAs("Recent values should have been emitted")
             .isGreaterThan(38);
      }
   }

   private void awaitUntilValueStabilizes(Supplier<List<Integer>> valueSupplier, Duration duration) {
      AtomicInteger lastValue = new AtomicInteger(0);
      Awaitility.await()
          .pollInterval(duration)
          .timeout(ofSeconds(20))
          .until(() -> {
             int newReceivedCount = valueSupplier.get().size();
             return newReceivedCount == lastValue.getAndSet(newReceivedCount);
          });
   }
}