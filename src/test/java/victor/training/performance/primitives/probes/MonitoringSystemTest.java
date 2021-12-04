package victor.training.performance.primitives.probes;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import victor.training.performance.primitives.probes.Probes.ValueAndDelay;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MonitoringSystemTest {



   @Test
   void discardsOldSamplesOnOverflow() {
      List<ValueAndDelay> responses = new ArrayList<>();
      for (int i = 0; i < 41; i++) responses.add(new ValueAndDelay(0, 2));
      for (int i = 0; i < 40; i++) responses.add(new ValueAndDelay(1, 2));
      Plotter plotter = new Plotter(10);
      MonitoringSystem target = new MonitoringSystem(new Probes(responses), plotter);

      target.start();

      AtomicInteger lastReceivedCount = new AtomicInteger(0);
      Awaitility.await().pollInterval(Duration.ofMillis(500))
          .timeout(Duration.ofSeconds(20))
          .until(() -> {
             int newReceivedCount = plotter.getReceivedValues().size();
//             log.debug("Last:{}, new:{}", lastReceivedCount.get(), newReceivedCount);
             return newReceivedCount == lastReceivedCount.getAndSet(newReceivedCount);
          });

      System.out.println(plotter.getReceivedValues());
      assertThat(plotter.getReceivedValues().stream().filter(v -> v == 0)).hasSizeLessThan(10);
      assertThat(plotter.getReceivedValues().stream().filter(v -> v == 1)).hasSizeGreaterThan(38);
   }
}