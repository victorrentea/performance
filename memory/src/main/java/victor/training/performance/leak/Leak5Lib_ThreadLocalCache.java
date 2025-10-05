package victor.training.performance.leak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big;

import static victor.training.performance.util.PerformanceUtil.kb;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
public class Leak5Lib_ThreadLocalCache {
  @GetMapping("leak5/lib")
  public String endpoint() throws NoSuchFieldException, IllegalAccessException {
    String work = Library.method();
    // what TODO ?
    sleepMillis(300); // my application logic
    boolean vt = false;
//    vt = Thread.currentThread().isVirtual();
    return "Manifests under high RPS on Virtual Threads"
           + (vt ? "" : "<br>⚠️⚠️⚠️ <span style='color:red'>NOT RUNNING ON A VIRTUAL THREAD ⚠️⚠️⚠️</span>")
        ;
  }

  //region Solution (you won't like it)
  private void clearThreadLocalsViaReflection() throws NoSuchFieldException, IllegalAccessException {
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
    return "A bit of work using " + getCachedLifeContext();
  }

  private static final ThreadLocal<LifeContext> threadLocal = new ThreadLocal<>();

  private static LifeContext getCachedLifeContext() {
    // JIRA-006 2010-03 Client threads probably return later as they are *probably* pooled
    //  => we will cache LifeContext in ThreadLocal
    if (threadLocal.get() != null) {
      return threadLocal.get();
    }
    LifeContext lifeContext = initLife();
    threadLocal.set(lifeContext);
    return lifeContext;
  }

  private static LifeContext initLife() {
    try {
      log.info("Searching for the Meaning of Life ...");
      sleepMillis(30); // takes a bit of time
      return new LifeContext(42, new Big(kb(100)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private record LifeContext(
      int meaningOfLife,
      Big knowledgeSchema) {}
}
