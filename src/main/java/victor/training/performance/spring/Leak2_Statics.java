package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("leak2")
@Slf4j
public class Leak2_Statics {

   // hand-made cache: NEVER
   private /*static*/ final Map<String, Integer> smallEntriesCantHurt = new HashMap<>();

   @GetMapping
   public String test() {
      log.info("Catch me if you can");
      for (int i = 0; i < 1_000; i++) {
         // simulate a lot more load with jmeter
         smallEntriesCantHurt.put(UUID.randomUUID().toString(), 1);
      }
      return "real-life case: no more obvious suspect 20MB int[] + only happens under stress test";
   }
}