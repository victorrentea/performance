package victor.training.spring.batch.core.extra;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;

public class CaptureStartTimeListener implements JobExecutionListener {
  @Override
  public void beforeJob(JobExecution jobExecution) {
    String startTimestamp = LocalDateTime.now().toString();
    jobExecution.getExecutionContext().put("START_TIME", startTimestamp);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {

  }
}
