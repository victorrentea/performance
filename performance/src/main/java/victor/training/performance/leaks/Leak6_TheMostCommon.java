package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("leak6")
public class Leak6_TheMostCommon {
   // AVOID: Hand-made cache!! Plugin a mature one!
   // [RO] Nu-ti faci cacheul de mana niciodata
   private Map<String, Object> toataBaza = new HashMap<>(); // singleton should not contain static maps

   @GetMapping
   public String test() {
      toataBaza.put(UUID.randomUUID().toString(), new BigObject20MB());
      return "the most brainless, but most common. Long Live SonarLint";
   }
}