package victor.training.performance.interview;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logging {
  public static void main(String[] args) {
    log.debug("Hello, World! {}", args); // 💖💖

    if (log.isDebugEnabled()) { // 90s NEVER again
      log.debug("Hello, World! " + args);
    }
    if (log.isDebugEnabled()) { // only reason
      log.debug("Hello, World! {}", jsonify(args));
    }
//    log.debug(() -> "Hello, World! "+args); // avoid

//    log.debug(() -> "Hello, World! "+jsonify(args)); // avoid
//    log.debug("Hello, World! {}", () -> jsonify(args));// in log4j2
  }

  private static String jsonify(String[] args) {
    return null;
  }
}
