package victor.training.performance.batch.sync;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyJobListener implements JobExecutionListener {

    @Value("#{jobExecutionContext['MY_START_TIME']}")
    private String startDate;

    @Override
    public void beforeJob(JobExecution jobExecution) {
//        System.out.println("exec id:" + jobExecution.getJobInstance().getInstanceId());
        jobExecution.getExecutionContext().put("MY_START_TIME", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // send OK emails to people
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // send ALert email to people
            System.out.println("DELETE FROM WHERE IMPORT_TIME="+startDate);
        }
    }
}
