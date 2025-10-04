package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {
  private List<Access> lastTenAccesses = new ArrayList<>();

  record Access(String ip, LocalDateTime timestamp) {
  }

  @GetMapping
  public synchronized String endpoint(HttpServletRequest request) {
    Access access = new Access(request.getRemoteAddr() + ":" + request.getRemotePort(), LocalDateTime.now());

    lastTenAccesses.add(access);
    if (lastTenAccesses.size() > 10) {
      lastTenAccesses = lastTenAccesses.subList(1, lastTenAccesses.size()); // skip first
    }
    return "The current window size is " + lastTenAccesses.size();
  }

  @GetMapping("many-calls")
  public String mass(HttpServletRequest request) {
    for (int i = 0; i < 10_000; i++) {
      endpoint(request); // close enough for our experiment
    }
    return "The current window size is " + lastTenAccesses.size() + ": " + lastTenAccesses;
  }
}

/**
 * ⭐️ KEY POINTS
 * - 🧠 Retained Heap = "How much does this object keep alive?"
 * - 😱 .subList() returns a view referencing the original list -> add/remove to a LinkedList/Queue
 * - 👍 Read the apidoc
 */

