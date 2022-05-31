package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("leak7")
public class Leak7_Cache {
   @Autowired
   private Stuff stuff;

   @GetMapping
   public String test() {
//      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://customer-service").queryParam("p1", "v1");
//      ResponseEntity<String> exchange = new RestTemplate().exchange(builder.toUriString(), HttpMethod.GET, null, String.class);

      BigObject20MB data = stuff.returnCachedDataForDay(LocalDateTime.now());
      return "Tools won't always shield you from mistakes: data=" + data + ", " + PerformanceUtil.getUsedHeap();
   }
}

@Service
@Slf4j
class Stuff {
   @Cacheable("missed-cache") // = a proxy intercepts the method call and returns the cached value for that parameter
   public BigObject20MB returnCachedDataForDay(LocalDateTime date) {
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return new BigObject20MB();
   }
}

/**
 * KEY POINTS
 * - [ideally] test that your caches work via automated tests (eg @SpringBootTest)
 */