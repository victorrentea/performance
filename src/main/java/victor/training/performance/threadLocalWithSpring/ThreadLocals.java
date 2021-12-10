package victor.training.performance.threadLocalWithSpring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.spring.threadscope.ClearableThreadScope;
import victor.training.performance.util.PerformanceUtil;

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

   @Bean
   public static CustomScopeConfigurer defineThreadScope() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("thread", new ClearableThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
      return configurer;
   }

}

@Slf4j
@Component
class Startup implements CommandLineRunner {
   @Autowired
   Layer1 layer1;
   @Autowired
   ThreadPoolTaskExecutor springPool;

   public void run(String... args) throws Exception {
      for (int i = 0; i <10; i++) {
         int j = i;
         springPool.submit(() -> {
            String u = "u" + j;
            log.debug("I am user " + u);
            layer1.method(u);
         });
      }

   }
}

@Component
@Scope(value = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserNameHolder {
//   public static ThreadLocal<String> currentUsername = new ThreadLocal<>(); // look for thread scoped beans in Spring .
   // Spring by default propagates SpringSecurityContext, @Transaction, HttpSession, CorrelationId, tenantId
   private String username;

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }
}

@Service
@RequiredArgsConstructor
class Layer1 {// controller
   private final Layer2 layer2;
   private final UserNameHolder userNameHolder;

   public void method(String u) {
      userNameHolder.setUsername(u);
      try {
         layer2.method();
      } finally {
         ClearableThreadScope.clearAllThreadData();
//         UserNameHolder.currentUsername.remove();
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
   @Autowired
   UserNameHolder userNameHolder;

   public void method() { // repository
      log.debug("UPDATE ... SET MODIFIED_BY=? " +userNameHolder.getUsername());
   }
}