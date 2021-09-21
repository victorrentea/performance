package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication(exclude = {
    // these stop Spring to connect to the database
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class BarmanApp {
   public static void main(String[] args) {
      SpringApplication.run(BarmanApp.class, args)
      // .close() // Note: .close to stop executors after CLRunner finishes
      ;
   }

   @Bean
   public static CustomScopeConfigurer defineThreadScope() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("thread", new SimpleThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
      return configurer;
   }

   @Autowired
   private PropagateRequestContext propagateRequestContext;

//      static ExecutorService pool = Executors.newFixedThreadPool(16);

   @Bean
   public ThreadPoolTaskExecutor executor(@Value("${barman.count}")int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(40);
      executor.setMaxPoolSize(40); // pp ca pana ieri faceam in acest thread pool doar cpu (eg image rendering)
      executor.setQueueCapacity(500);

      // core == max; de ce ? pentru a evita sa ridici presiunea pe sistemele DOWNSTREAM cand TU esti in focuri

      // size(queue) ~ max waiting time for your clients. queueSize/workers*avgTaskTime ~= queue waiting time. = 5 sec

      // core = ?
         // daca taskurile sunt CPU-bound ==> core <= # CPU
         // daca taskurile sunt IO-bound ==> experiment in iso-prod: nu dai cu 1000 de DB connection in severul.

      // rejected execution handler = leave default (exception) >> limit upstream impact (client of client)

      executor.setThreadNamePrefix("barman-");
      executor.initialize();
//      executor.setRejectedExecutionHandler(new CallerRunsPolicy());

      executor.setTaskDecorator(propagateRequestContext);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
}

