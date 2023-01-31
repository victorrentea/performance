package victor.training.performance.spring;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.symmetric.AES.CFB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("leak2")
@Slf4j
public class Leak2_Statics {

   private /*static*/ final Map<String, ALittleObject> anInnocentMap = new HashMap<>();

   @GetMapping
   public String test() { // TODO load test with jmeter
      ALittleObject obj = new ALittleObject("Some name", "email@example.com", 23);
      String id = UUID.randomUUID().toString();
      anInnocentMap.put(id, obj);
      CompletableFuture.runAsync(() -> longJob(id));
      return "More realistic: no more obvious 20MB int[] + only happens under stress test";
   }

   private void longJob(String id) {
      System.out.println("networkCall");
      ALittleObject data = anInnocentMap.remove(id);
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
