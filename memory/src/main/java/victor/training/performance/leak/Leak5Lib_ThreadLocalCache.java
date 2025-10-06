package victor.training.performance.leak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big;

import static victor.training.performance.util.PerformanceUtil.*;

@RestController
public class Leak5Lib_ThreadLocalCache {
  @GetMapping("leak5/lib")
  public String endpoint() throws NoSuchFieldException, IllegalAccessException {
    String work = Library.method();
    // no further use of lib
    sleepMillis(300); // my application logic
    return message();
  }

  private String message() {
    boolean vt = false;
//    vt = Thread.currentThread().isVirtual();
    return "Manifests under high RPS on Virtual Threads"
           + (vt ? "" : "<br>⚠️⚠️⚠️ <span style='color:red'>NOT RUNNING ON A VIRTUAL THREAD ⚠️⚠️⚠️</span>") +
           done();
  }

  //region Solution (you won't like it)
  private void clearLibraryThreadLocalsViaReflection() throws NoSuchFieldException, IllegalAccessException {
    // TODO first: try to upgrade the lib
    var field = Library.class.getDeclaredField("threadLocal");
    field.setAccessible(true);
    ThreadLocal<?> tl = (ThreadLocal<?>) field.get(null);
    if (tl != null) {
      tl.remove();
    }
  }
  //endregion
}

/** ⭐️ KEY POINTS
 * ☢️ ThreadLocal data can make Virtual Threads heavy again
 */

// --- Library code I cannot change 🔽 ---
class Library {
  private static final Logger log = LoggerFactory.getLogger(Library.class);

  public static String method() {
    return "A bit of work using " + getContextCachedOnThread();
  }

  private static final ThreadLocal<LibContext> threadLocal = new ThreadLocal<>();

  private static LibContext getContextCachedOnThread() {
    if (threadLocal.get() != null) {
      return threadLocal.get();
    }
    LibContext context = init();
    threadLocal.set(context); // JIRA-006 2010-03 client threads should return later as they are *probably* pooled
    return context;
  }

  private static LibContext init() {
    log.info("Init Lib⏱️ ...");
    sleepMillis(30); // takes a tiny bit of time
    return new LibContext(42, new Big(KB(100)));
  }

  private record LibContext(
      int meaningOfLife,
      Big knowledgeSchema) {}
}
