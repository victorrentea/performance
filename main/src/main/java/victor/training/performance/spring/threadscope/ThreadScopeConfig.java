package victor.training.performance.spring.threadscope;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.stereotype.Component;

@Configuration
public class ThreadScopeConfig {
   @Bean
   public static CustomScopeConfigurer defineThreadScope() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("thread", new SimpleThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
      return configurer;
   }

}

//@Aspect
//@Component
//class RemoveThreadScopedObj {
//   // dete from
//
//   @Around("@annotation(KafkaListener)")
//   public Object cleanu(ProceedingJoinPoint pjp) throws Throwable {
//      try {
//         return pjp.proceed();
//      } finally {
//// remove TH local
//      }
//   }
//}