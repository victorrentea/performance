package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class BarPoolConfig {
  @Autowired
  MeterRegistry meterRegistry;

  @Bean
  public ThreadPoolTaskExecutor barPool(@Value("${bar.pool.size}") int barPoolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(barPoolSize);
    executor.setMaxPoolSize(barPoolSize);
// OOMException - Q1: memorie cat ocupa 1 task din coada?
//    executor.setQueueCapacity(Integer.MAX_VALUE);
    // Q2: cat e tolerabil pt client sa aspte sa i se execute taskul?
      // a) prefera userul sa astepte 15 sec sau prefera o exceptie ?
      // b) fire-and-forget tasks (long running export eg)
    //    -> daca crapa sistemul cu taskul tau pending in memorie
    executor.setQueueCapacity(500);
    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setThreadNamePrefix("bar-");
//    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
