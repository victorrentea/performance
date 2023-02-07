package victor.training.performance.profile.showcase;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.profile.showcase.LoanApplication.ApprovalStep;
import victor.training.performance.profile.showcase.LoanApplication.Status;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanService {
  private final LoanApplicationRepo repo;
  private final CommentsApiClient commentsApiClient;
  // other methods that require @Transactional

  public LoanApplicationDto getLoanApplication(Long id) {
    LoanApplication loanApplication = repo.findById(id).orElseThrow();
    List<CommentDto> comments = commentsApiClient.getCommentsForLoanApplication(id); // takes ±40ms
    return new LoanApplicationDto(loanApplication, comments);
  }

  @EventListener(ApplicationStartedEvent.class)
  public void insertInitialData() {
    ApprovalStep step1 = new ApprovalStep().setName("Pre-Scan Client").setStatus(Status.APPROVED);
    ApprovalStep step2 = new ApprovalStep().setName("Credit Registry").setStatus(Status.DECLINED);
    repo.save(new LoanApplication()
            .setId(1L)
            .setTitle("4Porche")
            .setSteps(List.of(step1, step2)));
  }
}
