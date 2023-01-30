package victor.training.performance.concurrency;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;

class Credentials {
}

@RestController
public class OtherRaceConditon {

  private List<String> lastSearches = new ArrayList<>();
  private Map<String, Credentials> map = new HashMap<>();

  @GetMapping
  public void getMapping(String user) {
    String u = UUID.randomUUID().toString();
    Credentials data = fetchCredetialsForUser(u);
    synchronized (map) {
      map.put(user, data); // 200 ms
    }

    CompletableFuture.runAsync(() -> {
      synchronized (map) {
        Credentials remove = map.remove(user);
      }
    });
  }

  @NotNull
  private static Credentials fetchCredetialsForUser(String user) {
    return new Credentials();
  }
}
