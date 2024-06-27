package victor.training.spring.batch.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import victor.training.spring.batch.core.domain.CityRepo;
import victor.training.spring.batch.core.domain.PersonRepo;
import victor.training.spring.batch.util.PerformanceUtil;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
   @BeforeEach
   final void before() {
//      cityRepo.save(new City("City 1"));

//      for (int i = 1; i <= 4; i++) {
//         cityRepo.save(new City("City " + i));
//      }
   }
   @Test
   public void test() throws Exception {
      int N = 4;
      File dataFile = XmlFileGenerator.generateFile(N);
      Map<String, JobParameter> paramMap = Map.of("FILE_PATH", new JobParameter(dataFile.getAbsolutePath()));
      JobExecution run = launcher.run(job, new JobParameters(paramMap));
      while (run.getExitStatus().isRunning()) {
         PerformanceUtil.sleepMillis(1);
      }
      System.out.println("JOB FINISHED");
      assertThat(run.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
      assertThat(personRepo.count()).isEqualTo(N);
      assertThat(cityRepo.count()).describedAs("Number of cities")
          .isEqualTo(XmlFileGenerator.citiesNamesGenerated.size());

      int pgPort = postgres.getFirstMappedPort();
      System.out.println("Hit [ENTER] to finish test and destroy DB docker (running on port :" +pgPort+")...");
      System.in.read();
   }
}