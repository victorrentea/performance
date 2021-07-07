package victor.training.performance.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class MyChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
//        log.debug("Start chunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
//        log.debug("End chunk");
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
