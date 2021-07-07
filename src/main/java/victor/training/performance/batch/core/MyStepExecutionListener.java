package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Slf4j
public class MyStepExecutionListener implements  org.springframework.batch.core.StepExecutionListener {
    @Value("#{jobExecutionContext['START_TIME']}")
    private LocalDateTime startTime;
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.debug("step exec context: " + stepExecution.getExecutionContext());
        log.debug("Job exec context: " + stepExecution.getJobExecution().getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
