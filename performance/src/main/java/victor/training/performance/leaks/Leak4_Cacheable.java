package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.PerformanceUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("leak4")
public class Leak4_Cacheable {
   @Autowired
   private Stuff stuff;

   @GetMapping
   public String test() {
      BigObject20MB data = stuff.returnCachedDataForDay(LocalDateTime.now());
      return "Tools won't always shield you from mistakes: data=" + data + ", " + PerformanceUtil.getUsedHeap();
      // but they still offer max-size, expiration..
      // https://www.ehcache.org/documentation/2.8/configuration/cache-size.html
   }
}

@Service
@Slf4j
class Stuff {
   @Cacheable("stuff")
   public BigObject20MB returnCachedDataForDay(LocalDateTime timestamp) {
      log.debug("Fetch data for date: {}", timestamp.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }
}