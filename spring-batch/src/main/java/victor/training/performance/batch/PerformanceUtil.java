package victor.training.performance.batch;

import org.apache.commons.lang.RandomStringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

public class PerformanceUtil {
    static Random random = new Random();
    static List<String> position = new ArrayList<>();

    public static void sleepSomeTime(int min, int max) {
        sleepMillis(randomInt(min, max));
    }

    public static int randomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        return min + random.nextInt(max - min);
    }

    public static String randomString(int size) {
        return RandomStringUtils.randomAlphabetic(size);
    }

    public static void printJfrFile() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        Optional<String> jfrArg = arguments.stream()
//              .filter(a -> a.contains("StartFlightRecording"))
                .filter(a -> a.contains("jfr"))
                .findFirst();
        if (jfrArg.isPresent()) {
            String jfrArgValue = jfrArg.get();
            if (jfrArgValue.contains("file=")) {
                Matcher matcher = Pattern.compile("[^\"=]+.jfr").matcher(jfrArgValue.substring(jfrArgValue.indexOf("file=")));
                if (matcher.find()) {
                    System.out.println("Recording JFR in file: " + matcher.group(0));
                    long t0 = currentTimeMillis();
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            System.out.println("Program ended. Recorded JFR for " + (currentTimeMillis() - t0) + " millis in file: " + matcher.group(0));
                        }
                    });
                    return;
                }
            }
        }
        System.out.println("<JFR not started>");
    }

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

    public static void sleepNanos(int nanos) {
        try {
            Thread.sleep(0, nanos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static int cpu(int millis) {
        long t0 = System.currentTimeMillis();
        int sum = 0;
        while (System.currentTimeMillis() - t0 < millis) {
            sum += Math.sqrt(System.currentTimeMillis());
        }
        return sum;
    }

    public static int measureCall(Runnable r) {
        long t0 = currentTimeMillis();
        r.run();
        long t1 = currentTimeMillis();
        return (int) (t1 - t0);
    }

    public static Callable<Integer> measuring(Runnable r) {
        return () -> measureCall(r);
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

    public static void waitForEnter(String why) {
        System.out.println(why);
        waitForEnter();
    }

    public static void waitForEnter() {
        System.out.println("[ENTER] to continue");
        new Scanner(System.in).nextLine();
        System.out.println("Continue...");
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

    public static void onEnterExit() {
        new Thread(() -> {
            new Scanner(System.in).nextLine();
            System.out.println("ENTER detected. System.exit(0);");
            System.exit(0);
        }).start();
    }
}
