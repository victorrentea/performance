package victor.training.performance.batch.basic;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

public class MyJobListener implements org.springframework.batch.core.JobExecutionListener {
   @Value("${file.path}")
   FileSystemResource inputFile;
   @Override
   public void beforeJob(JobExecution jobExecution) {
      System.out.println("Verifici putin starea bazei");
      jobExecution.getExecutionContext().put("file.name.param", inputFile.getFile().getAbsolutePath());
   }

   @Override
   public void afterJob(JobExecution jobExecution) {
      if (jobExecution.getStatus() == BatchStatus.FAILED) {

         String fileName = (String) jobExecution.getExecutionContext().get("file.name.param");
         System.out.println("Sending EMAILS for failure : job execution id = " + jobExecution.getId() + " file name " + fileName);
         System.out.println("Move file to /error");
      }
   }
}
