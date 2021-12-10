package victor.training.performance.spring.threadscope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadScopeConfig {
   @Bean
   public static CustomScopeConfigurer defineThreadScope() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("thread", new ClearableThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
      return configurer;
   }

}
