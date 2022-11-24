package victor.training.performance.spring;

import lombok.Value;
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

   private final Map<String, ALittleObject> anInnocentMap = new HashMap<>();


   // ******************************
   //  DACA TE MANANCA SA FACI VREUN CACHE DE MANA TA. NU O FACE.
   //   foloseste copilul mamei tale un framework 15y ehcache,caffeine,redis
   // ******************************

//
//   public void cautaInCache(String key) {
//      ALittleObject entry = anInnocentMap.get(key);
//      if (entry.timestamp.isBefore(now.minusMInutes(5))) {
//         return null;
//      }
//   }
   @GetMapping
   public String test() { // TODO load test with jmeter
      ALittleObject obj = new ALittleObject("Some name", "email@example.com", 23);
      anInnocentMap.put(UUID.randomUUID().toString(), obj);
      return "More realistic: no more obvious 20MB int[] + only happens under stress test";
   }
}
@Value
class ALittleObject {
   String name;
   String email;
   int age;
}

/**
 * KEY POINTS
 * - Never accumulate in a collection arbitrary elements without considering eviction.
 * - If you need a cache, don't create your own => use a library with max-heap protection
 * - Your are in a Spring singleton, so static or not, it makes no diferrence
 * - Retained heap = the amount of memory that will be freed if an object is evicted
 */
