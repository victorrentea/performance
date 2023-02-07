package victor.training.performance.profile.showcase;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
}

