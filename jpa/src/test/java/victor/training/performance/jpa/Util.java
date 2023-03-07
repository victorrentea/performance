package victor.training.performance.jpa;

import java.lang.management.ManagementFactory;

import static java.lang.System.currentTimeMillis;

public class Util {

    public static int measureCall(Runnable r) {
        long t0 = currentTimeMillis();
        r.run();
        long t1 = currentTimeMillis();
        return (int) (t1 - t0);
    }

    public static void printUsedHeap(String label) {
        System.out.println(label + ": " + getUsedHeap());
    }

    public static String getUsedHeap() {
        System.gc();
        return "Used heap: " + formatSize(getUsedHeapBytes()).replace(",", " ");
    }

    public static String formatSize(long usedHeapBytes) {
        return String.format("%,d B", usedHeapBytes);
    }

    public static long getUsedHeapBytes() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }

}
