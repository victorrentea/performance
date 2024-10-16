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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
public class BatchAppTest {
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
  final void insertInitialCities() {
    // simulate existing data
//      cityRepo.save(new City("City 1"));
  }

  @Test
  public void test() throws Exception {
    int N = 4_000;
    File dataFile = XmlFileGenerator.generateFile(N);
    Map<String, JobParameter> paramMap = Map.of("FILE_PATH", new JobParameter(dataFile.getAbsolutePath()));
    JobExecution run = launcher.run(job, new JobParameters(paramMap));
    while (run.getExitStatus().isRunning()) {
      PerformanceUtil.sleepMillis(1);
    }
    System.out.println("JOB FINISHED");
//    int pgPort = postgres.getFirstMappedPort();
//    System.out.println("You can connect to the Postgres DB on port " + pgPort);
    System.out.println("[ENTER] to finish test and destroy DB...");
    System.in.read();

    assertThat(run.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(personRepo.count()).isEqualTo(N);
    assertThat(cityRepo.count()).describedAs("Number of cities")
        .isEqualTo(XmlFileGenerator.citiesNamesGenerated.size());
  }
}