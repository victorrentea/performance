package victor.training.performance.batch.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

class DummyTasklet implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(DummyTasklet.class);
    private final String step;

    public DummyTasklet(String step) {
        this.step = step;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (int token = 1; token < 100; token++) {
            log.info("Step:" + step + " token:" + token);
        }
        return RepeatStatus.FINISHED;
    }
}
