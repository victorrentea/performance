package victor.training.performance.batch.sync;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import victor.training.performance.batch.sync.domain.CityRepo;
import victor.training.performance.batch.sync.domain.PersonRepo;

import static org.assertj.core.api.Assertions.assertThat;
import static victor.training.performance.PerformanceUtil.sleepq;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
public class BatchAppTest extends AbstractTestcontainersTestBase{
   @Autowired
   Job job;
   @Autowired
   JobLauncher launcher;
   @Autowired
   PersonRepo personRepo;
   @Autowired
   CityRepo cityRepo;

   @Autowired
   JobRepository jobRepository;
   @Test
   public void test() throws Exception {
      int N = 40_000;
      XmlFileGenerator.generateFile(N);
      JobExecution run = launcher.run(job, new JobParameters());
      while (run.getExitStatus().isRunning()) {
         sleepq(1);
      }



      System.out.println("JOB FINISHED");
      assertThat(run.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
      assertThat(personRepo.count()).isEqualTo(N);
      assertThat(cityRepo.count()).isEqualTo(N/1000);
   }
}