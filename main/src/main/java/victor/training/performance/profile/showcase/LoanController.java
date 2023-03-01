package victor.training.performance.profile.showcase;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import victor.training.performance.profile.showcase.LoanApplication.Status;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("profile/showcase")
@RequiredArgsConstructor
public class LoanController {
   private final LoanService loanService;

   @GetMapping("{id}")
   public LoanApplicationDto getLoanApplication(@PathVariable Long id) {
      return loanService.getLoanApplication(id);
   }
   @GetMapping("{id}/status")
   public Status getLoanApplicationStatus(@PathVariable Long id) {
      return loanService.getLoanApplicationStatusForClient(id);
   }

   private final PaymentRepo paymentRepo;

   @PostMapping("payments/delta")
   public int getUnprocessedPayments(@RequestBody List<Long> newOrExistingPaymentIds) {
      HashSet<Long> set = new HashSet<>(newOrExistingPaymentIds);
      List<Long> allIdsInDb = paymentRepo.allIds();
      set.removeAll(allIdsInDb);
      return set.size();
   }
private final EntityManager em;
   @EventListener(ApplicationStartedEvent.class)
   @Transactional
   @Order(10)
   public void initPayments() {
      log.info("Persisting payments...");
      List<Long> dbData = LongStream.rangeClosed(1, 30_000).boxed().collect(toList());
      Collections.shuffle(dbData);
      List<Payment> data = dbData.stream().map(i -> new Payment().setId(i)).collect(toList());
//      for (int i = 0; i < data.size(); i++) {
//         if (i % 100 == 0) {
//            System.out.println(i);
//         }
//         paymentRepo.save(data.get(i));
//      }
      for (Payment datum : data) {
         em.persist(datum);
      }
      log.info("DONE");
   }


}

@Entity @Data

//@SequenceGenerator(name = "seq")
class Payment {
   @Id
//   @GeneratedValue(generator = "seq")
   private Long id;
   private LocalDate date;
   private Integer amount;

}

interface PaymentRepo extends JpaRepository<Payment, Long> {
   @Query("SELECT id FROM Payment")
   List<Long> allIds();
}