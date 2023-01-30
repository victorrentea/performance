package victor.training.performance.concurrency;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

class Credentials {
}

@RestController
public class OtherRaceConditon {

  private List<String> lastSearches = new ArrayList<>();
  private Map<String, Credentials> map = Collections.synchronizedMap(new HashMap<>()); // never postpones data, with the price of us blocking


//  private Map<String, Credentials> onlyUseForCaches = new ConcurrentHashMap<>(); // inconsistent in theory
  // DO NOT EVER IMPLEMENT YOUR OWN CACHING IN YOUR BACKYARD. NEVER. VERY RISKY:
  // caffeine, ehcache, memcache, hazelcast, redis,
  // - OOM
  // - eviction (TTL)
  // - monitorign (size in Grafana)
  // - concurrency
  // - disk offloading
  // - LRU, MRU (eviction policy when it's full) (max size reach)
  // - distributed caching



  @GetMapping
  // imagine 2 req with the same user jdoe
  public void getMapping(String user) {
    Credentials data = fetchCredetialsForUser(user);
//    map.put(user, data); // 200 ms

    CompletableFuture.runAsync(() -> {
      System.out.println("pasa data in plain sigth arg" + data);
//        Credentials remove = map.remove(user); // the first async task running this line
      // removes jdoe

      // the secodn task gets NULL from map
    });
  }

  @NotNull
  private static Credentials fetchCredetialsForUser(String user) {
    return new Credentials();
  }
}
