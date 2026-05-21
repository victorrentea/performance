package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BarmanConfig {
  @Autowired
  MeterRegistry meterRegistry;

  private static final ThreadLocal<String> TRACE_ID_TL = new ThreadLocal<>();
  @Bean
  public ThreadPoolTaskExecutor poolBar(@Value("${pool.bar.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize); // initial no of thread. keep == max
    executor.setMaxPoolSize(barPoolSize); // how to decide size?

    //If, when you're under pressure, you spawn more parallelism, in most backend systems that can actually
    // degrade performance, because most of the time it's somebody else's fault downstream.
    // If in front of a lot of load you raise the concurrency, you are at risk of making the whole thing go.
    // You're pushing slower by pushing harder on them.
//
    executor.setQueueCapacity(500); // how to decide: max memory to invest + max Δt waiting
//
////    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
//    executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
    executor.setTaskDecorator(new TaskDecorator() {
      @Override
      public Runnable decorate(Runnable runnable) {
        // runs in submitter thread 🔴
        String submittedTraceId = TRACE_ID_TL.get();
        return () -> {
          // runs in worker thread 🟢
          TRACE_ID_TL.set(submittedTraceId);
          try {
            runnable.run();
          } finally {
            TRACE_ID_TL.remove();
          }
        };
      }
    });
    executor.setThreadNamePrefix("bar-");
//    executor.ter
//    executor.setRejectedExecutionHandler(...);
    return executor;
  }
}
