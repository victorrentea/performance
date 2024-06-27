package victor.training.spring.batch.core;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;

public class StartListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("START_TIME",
            LocalDateTime.now().toString());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }
}
