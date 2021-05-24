package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class EhCacheEventLogger implements CacheEventListener<Object, Object> {
   @Override
   public void onEvent(CacheEvent<?, ?> cacheEvent) {
      log.info("Event {}: {}={} ",
          cacheEvent.getType(),
          cacheEvent.getKey(),
          cacheEvent.getNewValue());
   }
}