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
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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

   @Bean
   public ThreadPoolTaskExecutor beerPool() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(1);
      executor.setMaxPoolSize(1);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("beer-");
      executor.initialize();
      executor.setTaskDecorator(new TaskDecorator() {
         @Override
         public Runnable decorate(Runnable submittedWork) {
            // here I am in the submitter thread.
            long t0 = System.currentTimeMillis();
            return ()->{
               long t1 = System.currentTimeMillis();
               // here I am in the worker thread
               System.out.println("What is this: (millis): " + (t1-t0));
               submittedWork.run();
            };
         }
      });
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }

   @Bean
   public ThreadPoolTaskExecutor vodkaPool(@Value("${barman.count}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("vodka-");
      executor.initialize();
//      executor.reje
      executor.setTaskDecorator(propagateRequestContext);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
}

