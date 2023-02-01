package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("profile/javalock")
@RequiredArgsConstructor
public class Profile7_JavaLockContention {
    //  🛑 Double locking: synchronized {} and synchronizedList ~> Use ONE
  private final List<Long> recentTicketIds = Collections.synchronizedList(new ArrayList<>());

  @GetMapping("recent-tickets")
  public List<Long> getRecentTicketIds() {
    return recentTicketIds;
  }

  @GetMapping
  public synchronized BPMTicket fetchRandomTicket() {
    //  🛑 Lock contention ~> reduce the size of the protected critical section
    BPMTicket ticket = fetchRandomTicketFromBPM();
    // critical area vv
    recentTicketIds.remove(ticket.getId()); // BUG#7235 - avoid duplicates in list
    recentTicketIds.add(ticket.getId());
    if (recentTicketIds.size() > 10) recentTicketIds.remove(0);
    // critical area ^^
    return ticket;
  }

  private BPMTicket fetchRandomTicketFromBPM() {
    sleepMillis(50); // reasonable response time for a REST call
    return new BPMTicket((long) new Random().nextInt(100), "active");
  }
}

@Value
class BPMTicket {
  Long id;
  String status;
}


