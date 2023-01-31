package victor.training.performance.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Slf4j
public class GlowrootUtil {
  public static void deleteDatabase() throws IOException {
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
      log.info("Glowroot DB deleted");
    }
  }
}
