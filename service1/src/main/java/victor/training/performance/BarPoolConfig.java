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
    // # de threaduri
//    executor.setCorePoolSize(2);// tot timpul 2;
//    executor.setMaxPoolSize(7); // la stress 7
    // castigi 2.5 MB daca nu e stres
    // apelurile de DB sunt 2 paralel la acalmie. la stres sunt 7 => DB poate degrada perf
      // e posibil ca raspunsurile

    // de obicei sa fie egale pentru ca daca sub stres tu dai mai tare in cei pe care-i chemi,
    // e posibil ca EI sa se miste si mai rau, si sa degradeze si mai rau situatia globala
    executor.setCorePoolSize(barPoolSize);
    executor.setMaxPoolSize(barPoolSize);

    executor.setQueueCapacity(500); //? cat tolereaza clientul sa astepte. cat ocupa MEMORIE coada? fct de durata medie a procesarii si de nr de

    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // toarna-ti tu bere, daca nu e nici un th liber.

    executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
    executor.setThreadNamePrefix("bar-");
    executor.initialize();
    return executor;
  }
}
