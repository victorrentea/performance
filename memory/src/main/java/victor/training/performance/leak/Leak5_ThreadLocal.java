package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

import static victor.training.performance.util.PerformanceUtil.done;

@RestController
public class Leak5_ThreadLocal {
  private static final ThreadLocal<RequestContext> threadLocal = new ThreadLocal<>();

  record RequestContext(String currentUser, Big20MB big) {
  }

  @GetMapping("leak5")
  public String controllerMethod() {
    String currentUsername = "john.doe"; // from request header/JWT/http session
    threadLocal.set(new RequestContext(currentUsername, new Big20MB()));

    service();

    return "Magic can hurt " + done();
  } // 🔥 Leak1Load

  private void service() {
    repo();
  }

  private void repo() {
    String currentUsername = threadLocal.get().currentUser();
    System.out.println("UPDATE ... MODIFIED_BY=" + currentUsername);
  }
}

/** ⭐️ KEY POINTS
 * 🧠 ThreadLocal is used in BE to propagate invisible 'metadata':
 *   - Security Principal ± Rights
 *   - Observability: Logback MDC / Trace ID / OTEL Baggage
 *   - @Transactional/JDBC Connection ± Hibernate Session
 * 👍 Keep it small
 * ☢️ TL might remain attached to idle worker thread in a pool ~> Memory Leak
 * ☢️ TL might leak to the next task of the same worker
 * ☢️ TL might not propagate from submitter thread to worker thread(s)
 * 👍 Prefer framework-managed ThreadLocal data over creating your own: MDC, Baggage, SecurityContextHolder
 * 👍 On your own ThreadLocal: #set(..); try { <work> } finally {#remove();}
 */


