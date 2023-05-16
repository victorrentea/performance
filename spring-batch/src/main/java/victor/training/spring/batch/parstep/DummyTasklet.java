package victor.training.spring.batch.parstep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
class DummyTasklet implements Tasklet {
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
