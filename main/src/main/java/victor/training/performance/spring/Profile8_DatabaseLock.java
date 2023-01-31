package victor.training.performance.spring;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("profile/dblock")
@RequiredArgsConstructor
public class Profile8_DatabaseLock {
  private final TicketRepo ticketRepo;
  private final EntityManager entityManager;

  @EventListener(ApplicationStartedEvent.class)
  public void insert100Tickets() {
    ticketRepo.saveAll(IntStream.range(0,100).mapToObj(id -> new Ticket().setId(id)).collect(toList()));
  }

  @GetMapping
  @Transactional
  // TODO fire 10 parallel requests to this and study the flamegraph: where is the bottleneck?
  public BPMTicket getAndUpdateTicketStatus() {
    int ticketId = 1; // suppose a single ID gets a lot of heat..
    Ticket dbTicket = ticketRepo.findById(ticketId).orElseThrow();
    entityManager.lock(dbTicket, LockModeType.PESSIMISTIC_WRITE);
    // optimized duplicated retrieval by ensuring no two calls happen for the same entity
    BPMTicket bpmTicket = restGetFromBPM(ticketId);

    if (!dbTicket.getStatus().equals(bpmTicket.getStatus())) {
      dbTicket.setStatus(bpmTicket.getStatus());
    }

    return bpmTicket;
  }

  private BPMTicket restGetFromBPM(int ticketId) {
    sleepMillis(50); // reasonable response time for a REST call
    return new BPMTicket(ticketId, Math.random()<.5?"active":"inactive");
  }

}

@Getter
@Setter
@NoArgsConstructor
@Entity
class Ticket {
  @Id
  private Integer id;
  private String status = "active";
}

interface TicketRepo extends JpaRepository<Ticket, Integer> {
}



