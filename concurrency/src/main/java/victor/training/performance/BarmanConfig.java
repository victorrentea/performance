package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
@EnableAsync //
public class BarmanConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor poolBar(@Value("${pool.bar.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize); // core == max
    executor.setMaxPoolSize(barPoolSize); // how to decide size?

    executor.setQueueCapacity(500); // how to decide?

//    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));

    executor.setTaskDecorator(new TaskDecorator() {
      @Override
      public Runnable decorate(Runnable taskOriginal) {
        Map<String, String> tot = MDC.getCopyOfContextMap();
        return () -> { // sunt in worker thread
          MDC.setContextMap(tot);
          try {
            taskOriginal.run(); //delegate to originally submitted task
          } finally {
            MDC.clear();
          }
        };
      }
    });

    // executorul poate fi decorat mai departe sa COPIEZE METADATELE IN TH PARINTE IN COPIL
    executor.setThreadNamePrefix("bar-");
//    executor.setRejectedExecutionHandler(...);
    executor.initialize();
    return executor;
  }
}
