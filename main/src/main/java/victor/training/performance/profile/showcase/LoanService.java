package victor.training.performance.profile.showcase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.profile.showcase.LoanApplication.ApprovalStep;
import victor.training.performance.profile.showcase.LoanApplication.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoanService {
  private final LoanApplicationRepo repo;
  private final CommentsApiClient commentsApiClient;
  private final List<Long> recentLoanStatusQueried = new ArrayList<>();

  // other methods that require @Transactional

  public LoanApplicationDto getLoanApplication(Long id) {
    LoanApplication loanApplication = repo.findById(id).orElseThrow();
    List<CommentDto> comments = commentsApiClient.getCommentsForLoanApplication(id); // takes ±40ms
    LoanApplicationDto dto = new LoanApplicationDto(loanApplication, comments);
    log.trace("Loan app: " + loanApplication);
    return dto;
  }

  public synchronized Status getLoanApplicationStatusForClient(Long id) {
    LoanApplication loanApplication = repo.findById(id).orElseThrow();
    recentLoanStatusQueried.remove(id); // BUG#7235 - avoid duplicates in list
    recentLoanStatusQueried.add(id);
    while (recentLoanStatusQueried.size() > 10) recentLoanStatusQueried.remove(0);
    return loanApplication.getCurrentStatus();
  }

  public List<Long> getRecentLoanStatusQueried() {
    return new ArrayList<>(recentLoanStatusQueried);
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
