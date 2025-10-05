package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Slf4j
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {
  private List<Access> lastTen = new ArrayList<>();

  record Access(String ip, LocalDateTime timestamp) {
  }

  @GetMapping
  public synchronized String endpoint(HttpServletRequest request) {
    Access access = new Access(request.getRemoteAddr() + ":" + request.getRemotePort(), LocalDateTime.now());

    lastTen.add(access);
    if (lastTen.size() > 10) {
      lastTen = lastTen.subList(1, lastTen.size()); // remove first
    }
    return "lastTen.size = " + lastTen.size();
  }
  // TODO
  //  - give this endpoint some heat🔥 using Leak3Load.java
  //  - analyze retained heap using
  //    - visualVM
  //    - IntelliJ > Memory Snapshot > Biggest Objects >
  //      1st > [Enter] > 'Dominator Tree' tab
}

/**
 * ⭐️ KEY POINTS
 * - 🧠 Retained Heap = "How much heap does this object keep alive?"
 * - 😱 .subList() returns a view referencing the original list
 *   👍 add/remove to a LinkedList/Queue
 * - 👍 Read the apidoc
 */

