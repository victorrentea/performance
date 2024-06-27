package victor.training.spring.batch.core.extra;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Motivation: is useful to see the detailed SQL log captured by p6spy just for a single page.
 * After that, we disable it to minimize the impact of logging on measurements;
 */
@Slf4j
public class LogSqlForFirstChunkListener implements ChunkListener {
   private int chunksLeft = 2;
   @Override
   public void beforeChunk(ChunkContext context) {
      if (chunksLeft > 0) {
         log.info(" ---------------- CHUNK -----------------");
         setP6spyLogging(Level.DEBUG);
      } else {
         if (chunksLeft == 0) {
            log.warn("Suspending logging of SQL as logging itself might influence the performance");
         }
         log.trace(" ---------------- CHUNK -----------------");
         setP6spyLogging(Level.OFF);
      }
      chunksLeft--;
   }

   @Override
   public void afterChunk(ChunkContext context) {

   }

   public static void setP6spyLogging(Level level) {
      Logger p6spyLogger = (Logger) LoggerFactory.getLogger("p6spy");
      p6spyLogger.setLevel(level);
   }
   @Override
   public void afterChunkError(ChunkContext context) {

   }
}
