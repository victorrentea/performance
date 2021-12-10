package victor.training.performance.threadLocalWithSpring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class ThreadLocals {
   public static void main(String[] args) {
      SpringApplication.run(ThreadLocals.class, args);
   }

   @Bean
   public ThreadPoolTaskExecutor springPool(@Value("${spring.pool.size}") int springPoolSize) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(springPoolSize);
      executor.setMaxPoolSize(springPoolSize);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("spring-");
      executor.initialize();
      return executor;
   }

}

@Slf4j
@Component
class Startup implements CommandLineRunner {
   @Autowired
   Layer1 layer1;

   public void run(String... args) throws Exception {
      ExecutorService pool = Executors.newFixedThreadPool(10);
      for (int i = 0; i <10; i++) {
         int j = i;
         pool.submit(() -> {
            String u = "u" + j;
            log.debug("I am user " + u);
            layer1.method(u);
         });
      }

   }
}

class UserNameHolder {
   public static ThreadLocal<String> currentUsername = new ThreadLocal<>(); // look for thread scoped beans in Spring .

   // Spring by default propagates SpringSecurityContext, @Transaction, HttpSession, CorrelationId, tenantId
}

@Service
@RequiredArgsConstructor
class Layer1 {// controller
   private final Layer2 layer2;

   public void method(String u) {
      UserNameHolder.currentUsername.set(u);
      try {
         layer2.method();
      } finally {
         UserNameHolder.currentUsername.remove();
         // avoid a common mem leak = Thread Local + Thread Pools
      }
   }
}
@Service
@RequiredArgsConstructor
class Layer2 {
   private final Layer3 layer3;

   public void method() {
      PerformanceUtil.sleepSomeTime(10,20);
      layer3.method();
   }
}

@Service
@Slf4j
class Layer3 {
   public void method() { // repository
      log.debug("UPDATE ... SET MODIFIED_BY=? " + UserNameHolder.currentUsername.get());
   }
}