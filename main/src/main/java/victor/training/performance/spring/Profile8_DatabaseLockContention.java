package victor.training.performance.spring;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("profile/dblock")
@RequiredArgsConstructor
public class Profile8_DatabaseLockContention {
  private final TicketRepo ticketRepo;
  private final EntityManager entityManager;

  @EventListener(ApplicationStartedEvent.class)
  public void insert100Tickets() {
    ticketRepo.saveAll(IntStream.range(0,100).mapToObj(id -> new Ticket().setId((long) id)).collect(toList()));
  }

  @GetMapping("{ticketId}")
  @Transactional
  // TODO fire 10 parallel requests on the same id and study the flamegraph: where is the bottleneck?
  public BPMTicket getAndUpdateTicketStatus(@PathVariable Long ticketId) {
    Ticket dbTicket = ticketRepo.findById(ticketId).orElseThrow();
    entityManager.lock(dbTicket, LockModeType.PESSIMISTIC_WRITE);
    BPMTicket bpmTicket = restGetFromBPM(ticketId);

    // validate status transition
    if (!dbTicket.getStatus().equals(bpmTicket.getStatus())) {
      dbTicket.setStatus(bpmTicket.getStatus());
    }

    return bpmTicket;
  }

  private BPMTicket restGetFromBPM(long ticketId) {
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
  private Long id;
  private String status = "active";
}

interface TicketRepo extends JpaRepository<Ticket, Long> {
}



