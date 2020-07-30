package victor.training.performance.batch.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

class DummyTasklet implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(DummyTasklet.class);
    private final String stepName;

    public DummyTasklet(String step) {
        this.stepName = step;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
//        for (int token = 1; token < 100; token++) {
//            log.info("Step:" + step + " token:" + token);
//        }
        log.info("Running step " + stepName);
        Thread.sleep(1000);
//        if (true) throw new IllegalArgumentException("N-a fost bun fisierul");
        return RepeatStatus.FINISHED;
    }
}
