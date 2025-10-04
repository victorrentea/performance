package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

@RestController
public class Leak1_ThreadLocal {
  private static final ThreadLocal<Big20MB> threadLocal = new ThreadLocal<>();

  @GetMapping("leak1")
  public String controllerMethod() {
    String currentUser = "john.doe"; // extracted from request headers (pretend)
    // TODO why thread locals?
    Big20MB requestMetadata = new Big20MB();
    requestMetadata.setCurrentUser(currentUser);
    threadLocal.set(requestMetadata);

    serviceMethod();

    return "Magic can hurt you";
  }

  private void serviceMethod() {
    repoMethod();
  }

  private void repoMethod() {
    var requestMetadata = threadLocal.get();
    String username = requestMetadata.getCurrentUser();
    System.out.println("UPDATE .. SET .. MODIFIED_BY=" + username);
  }
}

/** ⭐️ KEY POINTS
 * 🧠 ThreadLocal is used to propagate metadata:
 *   - Security Principal & Rights -> SecurityContextHolder
 *   - Observability Log Metadata (MDC) / OTEL Baggage / TraceID
 *   - Transaction + JDBC Connection + Hibernate Session -> @Transactional
 * ☢️ ThreadLocal are dangerous:
 *   - Can leak to next task of a worker thread in a thread pool
 *   - Can make Virtual Threads heavy again (later on that)
 *   - Might not propagate from submitter thread to worker thread
 * 👍 Use framework-managed ThreadLocals over creating your own
 * 👍 On your own ThreadLocal tl, after tl.set(..); do try { .. } finally{tl.remove();}
 */


