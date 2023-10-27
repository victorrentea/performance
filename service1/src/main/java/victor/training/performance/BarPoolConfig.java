package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BarPoolConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor barPool(@Value("${bar.pool.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize); // default no of threads in this pool, save memory
    executor.setMaxPoolSize(barPoolSize); // how much it can grow: how to decide ?

    // scaling up the number of threads can HARM performance in a distributed microservices arch
    // systems you call with a higher load might degrade performance


    executor.setQueueCapacity(500); // how to decide?
    // can client tolerate this waiting time?
    // can all the pending tasks fit memory?
      // submit tasks that keep large objects, byte[] in memory BAD PRACTICE

    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setThreadNamePrefix("bar-");
    executor.initialize();
    return executor;
  }
}
