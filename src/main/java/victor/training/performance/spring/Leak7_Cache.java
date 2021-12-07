package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("leak7")
public class Leak7_Cache {
   @Autowired
   private Stuff stuff;

   @GetMapping
   public String test() {
      BigObject20MB data = stuff.returnCachedDataForDay(LocalDate.now());
      return "Tools won't always shield you from mistakes: data=" + data + ", " + PerformanceUtil.getUsedHeap();
   }
}

@Service
@Slf4j
class Stuff {
   @Autowired
   CacheManager cacheManager;

//   @Cacheable("missed-cache")
   public BigObject20MB returnCachedDataForDay(LocalDate timestamp) {
      Cache cache = cacheManager.getCache("missed-cache");
      ValueWrapper entry = cache.get(timestamp);
      if (entry != null) {
         return (BigObject20MB) entry.get();
      }
      log.debug("Fetch data for date: {}", timestamp.format(DateTimeFormatter.ISO_DATE));
      BigObject20MB result = new BigObject20MB();

      cache.put(timestamp, result);
      return result;
   }
}