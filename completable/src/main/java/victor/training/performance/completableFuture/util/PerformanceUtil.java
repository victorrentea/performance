package victor.training.performance.completableFuture.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class PerformanceUtil {
    static Random random = new Random();
    static List<String> position = new ArrayList<>();

    /**
     * Sleeps quietly (without throwing a checked exception)
     */
    public static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


    public static void log(String message) {
        int PAD_SIZE = 20;
        String line = new SimpleDateFormat("hh:mm:ss.SSS").format(new Date()) + " ";
        String pad;
        String threadName = Thread.currentThread().getName();
        if (position.contains(threadName)) {
            pad = String.format("%" + (1 + position.indexOf(threadName) * PAD_SIZE) + "s", "");
        } else {
            synchronized (PerformanceUtil.class) {
                pad = String.format("%" + (1 + position.size() * PAD_SIZE) + "s", "");
                System.out.println(line + pad + threadName);
                System.out.println(line + pad + "=============");
                position.add(threadName);
            }
        }
        System.out.println(line + pad + message);
    }

}
