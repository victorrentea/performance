package victor.training.performance.leak;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big20MB;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

@RestController
public class Leak14_ShutdownHook {
  @GetMapping("leak14")
  public String add() throws Exception {
    OldLib.stuff();
    return "♾️ Leak!";
  }
}

class OldLib { // in a .jar you cannot change
  // lib was designed in early 2000s to be used in a desktop/console/job app
  public static void stuff() {
    Big20MB big = new Big20MB();
    System.out.println("Created " + big);
    Runtime.getRuntime().addShutdownHook(new Thread(() ->
        System.out.println("Cleanup (file) at JVM exit " + big)));
  }
}

class HackyFix {
  public static void cleanHooks() throws Exception {
    Class<?> clazz = Class.forName("java.lang.ApplicationShutdownHooks");
    Field hooksField = clazz.getDeclaredField("hooks");

    // To work, add to VM args: --add-opens java.base/java.lang=ALL-UNNAMED
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