package victor.training.performance.batch.basic;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

public class MyJobListener implements org.springframework.batch.core.JobExecutionListener {
   @Override
   public void beforeJob(JobExecution jobExecution) {
      System.out.println("Verifici putin starea bazei");
   }

   @Override
   public void afterJob(JobExecution jobExecution) {
      if (jobExecution.getStatus() == BatchStatus.FAILED) {
         System.out.println("Sending EMAILS for failure : job execution id = " + jobExecution.getId());
         System.out.println("Move file to /error");
      }
   }
}
