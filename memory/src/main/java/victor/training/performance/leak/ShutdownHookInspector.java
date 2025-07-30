package victor.training.performance.leak;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class ShutdownHookInspector {
    public static void cleanHooks() throws Exception {
        // Get the class
        Class<?> clazz = Class.forName("java.lang.ApplicationShutdownHooks");

        // Get the field "hooks"
        Field hooksField = clazz.getDeclaredField("hooks");
        hooksField.setAccessible(true);

        // Get the map value (it's static, so pass null)
        IdentityHashMap<Thread, Thread> hooks = (IdentityHashMap<Thread, Thread>) hooksField.get(null);

        if (hooks != null) {
            Set<Thread> keys = new HashSet<>(hooks.keySet());
            for (Thread t : keys) {
                Runtime.getRuntime().removeShutdownHook(t);
            }
        } else {
            System.out.println("Shutdown already in progress or no hooks registered.");
        }
    }
}