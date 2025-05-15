package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.performance.threadpool.MonitorQueueWaitingTimeTaskDecorator;

@Configuration
public class BarmanConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor poolBar(@Value("${pool.bar.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(10); // core == max to not push harder on degrading downstream systems
//    executor.setCorePoolSize(10); // core == max to not push harder on degrading downstream systems
    executor.setCorePoolSize(barPoolSize); // core == max to not push harder on degrading downstream systems
    executor.setMaxPoolSize(barPoolSize); // how to decide size?

    executor.setQueueCapacity(500); // how to decide?

    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setThreadNamePrefix("bar-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
//    executor.setRejectedExecutionHandler(...);
    executor.initialize();
    return executor;
  }
}
