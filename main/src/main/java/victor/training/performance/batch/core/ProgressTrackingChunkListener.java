package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class ProgressTrackingChunkListener implements ChunkListener {
    @Value("#{stepExecutionContext['TOTAL_ITEM_COUNT']}")
    private int totalItems;
    private int lastPercent = -1;
    private LocalDateTime startTime = now().minusSeconds(1); // at step creation time (StepScoped)
    private LocalDateTime lastDisplayTime = now().minusHours(1);
    private static final int percentDisplayDeltaSeconds = 5;

    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution = context.getStepContext().getStepExecution();
        int totalRead = stepExecution.getReadCount() + stepExecution.getReadSkipCount();
        int newPercent = (int) Math.round(totalRead * 100d / totalItems);
        boolean passedEnoughTime = now().minusSeconds(percentDisplayDeltaSeconds).isAfter(lastDisplayTime);
        boolean lastChunk = totalRead == totalItems;
        if (passedEnoughTime && newPercent != lastPercent || lastChunk) {
            lastDisplayTime = now();
            lastPercent = newPercent;
            int speed = (int) (totalRead / startTime.until(now(), SECONDS));
            log.info("Progress: {}% done. Speed = {} items/s", newPercent, speed);
        }
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
