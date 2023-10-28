package victor.training.performance.spring;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.randomString;

@Slf4j
@RestController
@RequestMapping("leak6")
public class Leak6_TemporaryGlobal {
  // or 'static' - the same
  private final Map<String, UserProfile> userProfiles = Collections.synchronizedMap(new HashMap<>());

  /// they were adding data they got from an external system to this map,
  // in a structure containing a LocalDateTime retrieveTimestamp;

  // later they did v = map.get(key);
  // if (v.retrieveTimestamp is recent) { use v; } else { fetch from external system; put in map;!!!! BUT FIRST REMOVE THE OLD}
  // STUPID implem of TTL

  @GetMapping
  public String test(@RequestParam(defaultValue = "1") Long id) {
    String currentUser = randomUUID().toString();
    UserProfile userProfile = fetchCurrentUserProfile(currentUser);
    userProfiles.put(currentUser, userProfile);
    if (!userProfile.isActive()) {
      throw new IllegalArgumentException("Inactive user: action not allowed");
    }
    CompletableFuture.runAsync(() -> longProcessingAsync(id, currentUser));

    return "Realistic leak: no more obvious huge int[] => only happens under stress test <br>" +
           "Btw, it occurs only <a href='?id=-1'>conditionally</a>. <br>"+
           "Temporary profiles count: " + userProfiles.size();
  }

  private void longProcessingAsync(Long id, String username) {
    UserProfile userProfile = userProfiles.get(username);
    log.info("Job id={} START", id);
    anApiCall(id, userProfile.jurisdictions);
    userProfiles.remove(username);
    log.info("Job id={} END", id);
  }

  private void anApiCall(Long orderId, Map<String, List<String>> jurisdictions) {
    log.info("Network call for "+ orderId);
    if (orderId < 0) {
      throw new RuntimeException("Cam you find me in the log?😨");
    }
  }

  public UserProfile fetchCurrentUserProfile(String username) { // network call
    List<String> jurisdictions = IntStream.range(0, 100).mapToObj(i -> randomString(20)).collect(toList());
    return new UserProfile("John Doe", true, Map.of("APP1", jurisdictions));
  }

  @Value // multithreading + immutable = ❤️
  private static class UserProfile {
    String fullName;
    boolean active;
    Map<String, List<String>> jurisdictions;
  }
}

/**
 * KEY POINTS
 * - Making a field 'static' in a Spring singleton it makes no difference
 * - Temporary mutable state was always a bad idea
 * - Never accumulate in a collection elements without making sure they are evicted
 * - If you need a cache, don't create your own => use a library with max-heap protection
 * - Retained heap = the amount of memory that will be freed if an object is evicted
 * - max retained heap will point to the instance of this class storing the data
 */
