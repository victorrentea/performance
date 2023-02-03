package victor.training.performance.spring;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import victor.training.performance.spring.LoanApplication.ApprovalStep;
import victor.training.performance.spring.LoanApplication.Status;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("profile/showcase")
@RequiredArgsConstructor
public class ProfiledController {
   private final ProfiledService profiledService;

   @GetMapping("{id}")
   public LoanApplicationDto getLoanApplication(@PathVariable Long id) {
      return profiledService.getLoanApplication(id);
   }
}

@Service
@Transactional
@RequiredArgsConstructor
class ProfiledService {
   private final LoanApplicationRepo repo;
   private final CommentsServiceApi commentsServiceApi;
   // other methods that require @Transactional

   public LoanApplicationDto getLoanApplication(Long id) {
      LoanApplication loanApplication = repo.findById(id).orElseThrow();
      List<CommentDto> comments = commentsServiceApi.getCommentsForLoanApplication(id);
      return new LoanApplicationDto(loanApplication, comments);
   }

   @EventListener(ApplicationStartedEvent.class)
   public void insertInitialData() {
      ApprovalStep step1 = new ApprovalStep().setName("Pre-Scan Client").setStatus(Status.APPROVED);
      ApprovalStep step2 = new ApprovalStep().setName("Credit Registry").setStatus(Status.DECLINED);
      repo.save(new LoanApplication()
              .setId(1L)
              .setTitle("4Porche")
              .setSteps(List.of(step1,step2)));
   }
}

@Value
class LoanApplicationDto {
   Long id;
   String title;
   Status globalStatus;
   List<String> comments;

   public LoanApplicationDto(LoanApplication loanApplication, List<CommentDto> comments) {
      id = loanApplication.getId();
      title= loanApplication.getTitle();
      globalStatus = loanApplication.getCurrentStatus();
      this.comments = comments.stream().map(CommentDto::getBody).collect(toList());
   }
}

@FeignClient(value = "loan-comments", url = "http://localhost:9999/")
interface CommentsServiceApi {

   @RequestMapping(method = RequestMethod.GET, value = "loan-comments/{id}")
   List<CommentDto> getCommentsForLoanApplication(@PathVariable Long id);
}
@Data
class CommentDto {
   private String body;
}

@Getter
@Setter
@Entity
class LoanApplication {
   enum Status {NOT_STARTED, PENDING, APPROVED, DECLINED}
   @Id
   private Long id;
   private String title;
   @ElementCollection
   private List<ApprovalStep> steps = new ArrayList<>();

   public Status getCurrentStatus() {
      return getLastStep().getStatus();
   }
   private ApprovalStep getLastStep() {
      List<ApprovalStep> startedSteps = steps.stream().filter(ApprovalStep::isStarted).collect(toList());
      if (startedSteps.isEmpty()) return steps.get(0);
      return startedSteps.get(startedSteps.size() -1);
   }
   @Embeddable
   @Data
   public static class ApprovalStep {
      private String name;
      private Status status;

      boolean isStarted() {
         return status != Status.NOT_STARTED;
      }
   }

}

interface LoanApplicationRepo extends JpaRepository<LoanApplication, Long> {
}

