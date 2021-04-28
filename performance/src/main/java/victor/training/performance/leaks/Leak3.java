package victor.training.performance.leaks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("leak3")
public class Leak3 {
   @Autowired
   private UserContext userData;

   @GetMapping
   public String test() throws Exception {
      String uuid = UUID.randomUUID().toString();
      userData.tryCache(uuid);
      return "Spring magic: know your frameworks!";
   }
}

@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext implements Serializable {

   private Map<String, BigObject20MB> cache = new HashMap<>();

   public BigObject20MB tryCache(String key) {
      if (cache.containsKey(key)) {
         return cache.get(key); // cache hit
      }
      BigObject20MB newObject = loadData();
      cache.put(key, newObject);
      return newObject;
   }

   public BigObject20MB loadData() {
      return new BigObject20MB();
   }
}
