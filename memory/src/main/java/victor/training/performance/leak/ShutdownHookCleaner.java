package victor.training.performance.leak;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class ShutdownHookCleaner {
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