package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

import java.time.LocalDateTime;

@RestController
public class Leak1_ThreadLocal {
  private static final ThreadLocal<RequestContext> threadLocal = new ThreadLocal<>();

  record RequestContext(
      String currentUser,
      Big20MB big // demo
  ) {
  }

  @GetMapping("leak1")
  public String controllerMethod() {
    String currentUsername = "john.doe"; // extracted from request headers/http session/JWT
    RequestContext requestContext = new RequestContext(currentUsername, new Big20MB());
    threadLocal.set(requestContext);

    service();

    return "Magic can hurt " + LocalDateTime.now();
  } // 🔥 Leak1Load

  private void service() {
    repo();
  }

  private void repo() {
    var requestContext = threadLocal.get();
    String currentUsername = requestContext.currentUser();
    System.out.println("UPDATE .. SET .. MODIFIED_BY=" + currentUsername);
  }
}

/** ⭐️ KEY POINTS
 * 🧠 ThreadLocal (TL) is used in BE to propagate invisible 'metadata':
 *   - Security Principal & Rights -> SecurityContextHolder
 *   - Observability: Log Metadata (MDC) / OTEL Baggage / TraceID
 *   - Transaction + JDBC Connection + Hibernate Session by @Transactional
 * 👍 Keep it small ⚠️
 * ☢️ TL might remain attached to worker thread in a pool ~> Leak
 * ☢️ TL might leak to the next task of worker
 * ☢️ TL might not propagate from submitter thread to worker thread
 * 👍 Use framework-managed ThreadLocal data over creating your own: MDC, Baggage, SecurityContextHolder
 * 👍 On your own ThreadLocal tl: tl.set(..); then try { ... } finally{tl.remove();}
 */


