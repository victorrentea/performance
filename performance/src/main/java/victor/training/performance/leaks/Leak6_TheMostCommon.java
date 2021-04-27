package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("leak6")
@Slf4j
public class Leak6_TheMostCommon {

   // NEVER hand-make your own cache, plugin a professional one
   private static final Map<String, Integer> smallEntriesCantHurt = new HashMap<>();

   @GetMapping
   public String test() {
      log.info("Catch me if you can");
      for (int i = 0; i < 1_000; i++) {
         // simulate a lot more load --< hit with jmeter
         String s = UUID.randomUUID().toString();
         smallEntriesCantHurt.put(s, 128);
      }
      return "real-life case: no more obvious suspect 20MB int[]";
   }
}