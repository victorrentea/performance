package victor.training.performance.spring.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
// from https://medium.com/@iliamsharipov_56660/spring-boot-actuator-for-concurrentmapcache-2c7f0d290934
public class MetereredSimpleCacheConfig extends CachingConfigurerSupport {
   @Value("${spring.cache.cache-names}")
   private List<String> cacheNames;

   @Bean
   @Override
   public CacheManager cacheManager() {
      SimpleCacheManager cacheManager = new SimpleCacheManager();

      cacheManager.setCaches(cacheNames.stream()
          .peek(name -> log.info("Decorating simple cache " + name))
          .map(ConcurrentMapCacheMetricsWrapper::new).collect(toList()));
      return cacheManager;
   }

}
