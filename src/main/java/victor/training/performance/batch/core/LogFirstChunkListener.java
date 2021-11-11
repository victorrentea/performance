package victor.training.performance.batch.core;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Motivation: is useful to see the detailed SQL log captured by p6spy just for a single page.
 * After that, we disable it to minimize the impact of logging on measurements;
 */
@Slf4j
public class LogFirstChunkListener implements ChunkListener {
   private Boolean logEnabled;
   @Override
   public void beforeChunk(ChunkContext context) {
      if (logEnabled == null) {
         log.info("First Chunk Starting: ENABLE p6spy LOG");
         log.info(" ---------------- START OF CHUNK -----------------");
         setLoggingLevel(Level.DEBUG);
         logEnabled = true;
      } else if (logEnabled == true) {
         log.info(" ---------------- END OF  CHUNK -----------------");
         log.info("Second Chunk Starting: DISABLE p6spy LOG");
         setLoggingLevel(Level.OFF);
         logEnabled = false;
      }
   }

   @Override
   public void afterChunk(ChunkContext context) {

   }

   public static void setLoggingLevel(Level level) {
      ch.qos.logback.classic.Logger p6spyLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
          "p6spy");
      p6spyLogger.setLevel(level);
   }
   @Override
   public void afterChunkError(ChunkContext context) {

   }
}
