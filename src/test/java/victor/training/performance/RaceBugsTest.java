package victor.training.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.TimingExtension;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, TimingExtension.class})
class RaceBugsTest {
   private static final Logger log = LoggerFactory.getLogger(RaceBugsTest.class);
   private static final int N = 20_000;
   private static final List<Integer> ids = IntStream.range(0, N).boxed().collect(toList());
   private ExternalDependencyFake dependency = new ExternalDependencyFake(N);
   private RaceBugs target = new RaceBugs(dependency);


   @Test
   void countAlivePopulation() throws Exception {
      int actual = target.countAlive(ids);

      assertThat(actual).isEqualTo(N / 2);
   }

   @Test
   void retrieveEmails() throws Exception {
      assertThat(target.retrieveEmails(ids)).hasSize(N);
   }

   @Test
   void retrieveEmailsHalfDuplicates() throws Exception {
      dependency.withOverlappingEmails();

      assertThat(target.retrieveEmails(ids)).hasSize(N / 2);
   }

   @Test
   void retrieveEmailsHalfDuplicates_andChecked() throws Exception {
      dependency.withOverlappingEmails();
      dependency.withCheckingEmails();

      assertThat(target.retrieveEmails(ids)).hasSize(N / 4);
      assertThat(dependency.emailChecksPerformed())
          .describedAs("Should perform minimum number of email checks, to reduce costs")
          .isEqualTo(N / 2);
   }

}