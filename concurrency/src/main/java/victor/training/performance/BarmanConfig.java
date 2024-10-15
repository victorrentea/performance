package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.performance.threadpool.MonitorQueueWaitingTimeTaskDecorator;

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

    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    // executorul poate fi decorat mai departe sa COPIEZE METADATELE IN TH PARINTE IN COPIL
    executor.setThreadNamePrefix("bar-");
//    executor.setRejectedExecutionHandler(...);
    executor.initialize();
    return executor;
  }
}
