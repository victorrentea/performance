package victor.training.performance.leak;

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

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
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
    ticketRepo.saveAll(IntStream.rangeClosed(1,10).mapToObj(id -> new Ticket().setId((long) id)).collect(toList()));
  }

  @GetMapping("{ticketId}")
  @Transactional
  // TODO fire 10 parallel requests on the same id and study the flamegraph: where is the bottleneck?
  public BPMTicket getAndUpdateTicketStatus(@PathVariable Long ticketId) {
    Ticket dbTicket = ticketRepo.findById(ticketId).orElseThrow();
    //  🛑 Lock contention ~> reduce the size of the protected critical section
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

@Value
class BPMTicket {
  Long id;
  String status;
}

@Getter
@Setter
@NoArgsConstructor
@Entity
class Ticket {
  @Id
  private Long id;
  private String status = "active";
  private Long creatorUserId;
  private Long assignedUserId;
}

interface TicketRepo extends JpaRepository<Ticket, Long> {
}



