package victor.training.performance.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import victor.training.performance.jpa.repo.UberRepo;

@Slf4j
@Service
@RequiredArgsConstructor
public class Export {
  private final UberRepo uberRepo;
  public void export() {
    uberRepo.streamAll();
  }
}
