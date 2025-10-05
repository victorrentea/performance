package victor.training.performance.leak;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import static victor.training.performance.util.PerformanceUtil.done;

@RestController
public class Leak10_ShutdownHook {
  @GetMapping("leak10")
  public String add() throws Exception {
    return "♾️ Leak doing " + OldLib.doWork() + " " + done();
  }
}

// 🔽 in a .jar you cannot change
class OldLib {
  // lib was designed in the early 2000s to be used in a desktop/console/job app
  public static String doWork() {
    Big20MB big = new Big20MB();
    Runtime.getRuntime().addShutdownHook(new Thread(() ->
        System.out.println("Cleanup: " + big)));
    return "Work";
  }
}

class HackyFix {
  public static void cleanHooksUsingReflection() throws Exception {
    Class<?> clazz = Class.forName("java.lang.ApplicationShutdownHooks");
    Field hooksField = clazz.getDeclaredField("hooks");

    // Requires VM args: --add-opens java.base/java.lang=ALL-UNNAMED
    hooksField.setAccessible(true);

    IdentityHashMap<Thread, Thread> hooks = (IdentityHashMap<Thread, Thread>) hooksField.get(null);
    if (hooks == null) {
      return;
    }
    Set<Thread> keys = new HashSet<>(hooks.keySet()); // avoid ConcurrentModificationException
    for (Thread t : keys) {
      Runtime.getRuntime().removeShutdownHook(t);
    }
  }
}