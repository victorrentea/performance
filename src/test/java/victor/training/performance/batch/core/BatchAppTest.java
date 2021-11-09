package victor.training.performance.batch.core;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static victor.training.performance.util.PerformanceUtil.sleepq;

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
      int N = 4_000;
      File dataFile = XmlFileGenerator.generateFile(N);
      Map<String, JobParameter> paramMap = Map.of("FILE_PATH", new JobParameter(dataFile.getAbsolutePath()));
      JobExecution run = launcher.run(job, new JobParameters(paramMap));
      while (run.getExitStatus().isRunning()) {
         sleepq(1);
      }
      System.out.println("JOB FINISHED");
      assertThat(run.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
      assertThat(personRepo.count()).isEqualTo(N);
      assertThat(cityRepo.count()).isEqualTo(N/1000);
   }
}