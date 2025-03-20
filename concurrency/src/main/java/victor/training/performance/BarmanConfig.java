package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
public class BarmanConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor threadPool(@Value("${pool.bar.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize); // core == max
    executor.setMaxPoolSize(barPoolSize); // how to decide size?
    executor.setQueueCapacity(500); // how to decide?
//    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

//    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setTaskDecorator(new TaskDecorator() {
      @Override
      public Runnable decorate(Runnable task) {
        // in the thread calling .submit
        Map<String, String> map = MDC.getCopyOfContextMap();
        return ()-> {// thread hopping
          // in the worker thread
          MDC.setContextMap(map);
          task.run();
        };
      }
    });
    executor.setThreadNamePrefix("bar-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.initialize();
    return executor;
  }
}
