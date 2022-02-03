package victor.training.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.concurrency.ExternalDependencyFake;
import victor.training.performance.concurrency.RaceBugs;
import victor.training.performance.util.TimingExtension;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, TimingExtension.class})
class RaceBugsTest {
   private static final int N = 20_000;
   private static final List<Integer> ids = IntStream.range(0, N).boxed().collect(toList());
   private ExternalDependencyFake dependency = new ExternalDependencyFake(N);
   private RaceBugs target = new RaceBugs(dependency);


   @Test
   void retrieveEmails() throws Exception {
      assertThat(target.retrieveEmailsInParallel(ids)).hasSize(N);
   }

   @Test
   void retrieveEmailsHalfDuplicates() throws Exception {
      dependency.setHalfOverlappingEmails();

      assertThat(target.retrieveEmailsInParallel(ids)).hasSize(N / 2);
   }

   @Test
   void retrieveEmailsHalfDuplicates_andChecked() throws Exception {
      dependency.setHalfOverlappingEmails();
      dependency.setCheckingEmails();

      assertThat(target.retrieveEmailsInParallel(ids)).hasSize(N / 4);
      assertThat(dependency.emailChecksPerformed())
          .describedAs("Should perform minimum number of email checks, to reduce costs")
          .isEqualTo(N / 2);
   }

}