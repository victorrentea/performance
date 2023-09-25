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
  // Spring rewrite of the Java SE Thread Pool
  public ThreadPoolTaskExecutor barPool(@Value("${bar.pool.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize);
    executor.setMaxPoolSize(barPoolSize); // max != min makes sense:
    //  if you scaling up #threads DOES NOT PUSH HARDER ON OTHERS (eg for CPU work)
    // but you can still harm OTHER requests in execution on the same server (heat up your CPU)

    //   let's upgrade the discussion to ☁️ era: you run a batch job ALONE on its own machine pod started / killed after
    // it is risky to handle BOTH http requests AND batch jobs/MQ.



    executor.setQueueCapacity(500); // how to decide?
    // too small eg 10 => ERRORS at submit() => rejects work  (500 code)
    // too large eg 10K => if there is some SYNC call (REST) waiting for the result, the waiting time might exceed conn timeout (eg 1m)
    // larger => more memory occupied

//    executor.setTaskDecorator(new PropagateThreadScope());
    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setThreadNamePrefix("bar-");
    executor.initialize();
    return executor;
  }
}
