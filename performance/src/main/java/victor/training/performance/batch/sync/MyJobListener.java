package victor.training.performance.batch.sync;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("MY_START_TIME", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // send OK emails to people
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // send ALert email to people
        }
    }
}
