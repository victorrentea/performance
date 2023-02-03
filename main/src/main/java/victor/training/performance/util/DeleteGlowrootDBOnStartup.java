package victor.training.performance.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Slf4j
@Component
public class DeleteGlowrootDBOnStartup {
  @EventListener(ApplicationStartedEvent.class)
  public void onStartup() throws IOException {
    // otherwise Glowroot preserves its data over a restart
    String glowrootPath = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
            .filter(jvmArg -> jvmArg.startsWith("-javaagent:") && jvmArg.endsWith("glowroot.jar"))
            .map(jvmArg -> jvmArg.replace("-javaagent:", "").replace("glowroot.jar", ""))
            .findFirst()
            .orElse(null);
    if (glowrootPath == null) {
      log.warn("Glowroot agent not found: no database to wipe out");
      return;
    }
    log.debug("Glowroot agent found running from path: " + glowrootPath);
    File dataDir = new File(glowrootPath + "data");
    if (dataDir.isDirectory()) {
      FileUtils.deleteDirectory(dataDir);
      log.info("Glowroot DB successfully deleted");
    }
  }
}
