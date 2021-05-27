package victor.training.performance.batch.sync;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
public class BatchAppTest extends AbstractTestcontainersTestBase{
   @Autowired
   Job job;

   @Autowired
   JobLauncher launcher;
   @Test
   public void test() throws Exception {
      DataFileGenerator.generateFile(10_000);
//      int dt = measureCall(() -> SpringApplication.run(BatchApp.class, args).close());
//      System.out.println("Batch took " + dt + " ms");

      System.out.println("------------------------------");

      launcher.run(job, new JobParameters());
   }
}