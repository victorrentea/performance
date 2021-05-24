package victor.training.performance.batch.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

@Slf4j
public class MyStepExecutionListener implements  org.springframework.batch.core.StepExecutionListener {
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
