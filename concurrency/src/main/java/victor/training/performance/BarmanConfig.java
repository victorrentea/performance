package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.performance.threadpool.MonitorQueueWaitingTimeTaskDecorator;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

@Configuration
public class BarmanConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor poolBar(@Value("${pool.bar.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize); // core == max
    executor.setMaxPoolSize(barPoolSize); // how to decide size?

    executor.setQueueCapacity(500); // what queue is this?
    // when there are more tasks than threads
    // how to decide the number?
    // if too large => OOME and loooooong waiting time (increase the latency)
    // if too small => Rejected tasks 500 to clients

    // PTSD:
//    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));

    executor.setTaskDecorator(new TaskDecorator() {
      @Override
      public Runnable decorate(Runnable runnable) {
        // here I am in the parent thread
        String tenantId = MDC.get("tenantId");
        return () -> {
          // here I am in the child thread (worker)
          MDC.put("tenantId", tenantId); // restore the context
          try {
            runnable.run();
          } finally {
            MDC.remove("tenantId");
          }
        };
      }
    });
    executor.setThreadNamePrefix("bar-");
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());


    executor.initialize();
    return executor;
  }
//  @Provides
//  @Singleton
//  public ThreadPoolExecutor barPool() {
//     return new ThreadPoolExecutor(barPoolSize, barPoolSize, 0L, TimeUnit.MILLISECONDS,
//         new LinkedBlockingQueue<>(500), new ThreadFactory() {
//       // JavaSE equivalent of the above:
//   }
}
