package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("profile/javalock")
@RequiredArgsConstructor
public class Profile7_JavaLock {
  private final TicketRepo ticketRepo;

  private final Deque<Integer> recentTicketIds = new LinkedList<>();

  @GetMapping("recent")
  public Deque<Integer> getRecentTicketIds() {
    return recentTicketIds;
  }

  @GetMapping
  // TODO fire 20 parallel requests on this and study the JFR recording in Java Mission Control
  public BPMTicket fetchRandomTicket() {
    return fetchTicket(new Random().nextInt(100));
  }

  private synchronized BPMTicket fetchTicket(int ticketId) {
    // critical area vv
    recentTicketIds.remove(ticketId); // BUG#7235 2017
    recentTicketIds.addFirst(ticketId);
    if (recentTicketIds.size() > 10) recentTicketIds.removeLast();
    // critical area ^^
    return restGetFromBPM(ticketId);
  }

  private BPMTicket restGetFromBPM(int ticketId) {
    sleepMillis(50); // reasonable response time for a REST call
    return new BPMTicket(ticketId, "active");
  }

}

@Value
class BPMTicket {
  int id;
  String status;
}



