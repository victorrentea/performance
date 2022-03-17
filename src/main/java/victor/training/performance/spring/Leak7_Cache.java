package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
      String data = stuff.returnCachedDataForDay(LocalDateTime.now());
      return "Tools won't always shield you from mistakes: data=" + data + ", " + PerformanceUtil.getUsedHeap();
   }
}

@Service
@Slf4j
class Stuff {
   @Cacheable("missed-cache") // pune un proxy in fata acestei metode care, daca vede ca invoci metoda
   // cu aceiasi param ca adineauri, iti da din cache
   public String returnCachedDataForDay(LocalDateTime date) { // trebuia sa fi dat LocalDate nu LocalDateTime
      log.debug("Fetch data for date: {}", date.format(DateTimeFormatter.ISO_DATE));
      return date.toString();
   }
}