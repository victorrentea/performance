package victor.training.performance.util;

import lombok.SneakyThrows;
import org.apache.commons.lang.RandomStringUtils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;
import static java.time.format.DateTimeFormatter.ofPattern;

public class PerformanceUtil {
  static Random random = new Random();
  static List<String> position = new ArrayList<>();

  public static void sleepSomeTime() {
    sleepSomeTime(10, 100);
  }

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
  public static void sleepSeconds(long seconds) {
    sleepMillis(seconds * 1000);
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
    System.out.println("Resuming...");
  }

  public static String getUsedHeapHuman() {
    return "Used Heap: " + human(getUsedHeapBytes());
  }

  public static String getProcessMemoryHuman() {
    return "Process RAM: " + human(osRssBytes()) + "=RSS";
  }

  public static long osRssBytes() {
    long pid = ProcessHandle.current().pid();
    String os = System.getProperty("os.name").toLowerCase();

    // 1) Linux: /proc/self/status -> VmRSS: <KB>
    if (os.contains("linux")) {
      try {
        var lines = java.nio.file.Files.readAllLines(java.nio.file.Path.of("/proc/self/status"));
        for (var l : lines) {
          if (l.startsWith("VmRSS:")) {
            var m = java.util.regex.Pattern.compile("\\d+").matcher(l);
            if (m.find()) return Long.parseLong(m.group()) * 1024L; // kB -> bytes
          }
        }
      } catch (Exception ignore) { /* fall back */ }
    }

    // 2) macOS/Linux fallback: ps -o rss= -p <pid>  (RSS in kB)
    if (!os.contains("win")) {
      try {
        var out = runAndRead("ps", "-o", "rss=", "-p", String.valueOf(pid));
        if (!out.isBlank()) return Long.parseLong(out.trim()) * 1024L;
      } catch (Exception ignore) { /* fall back */ }
    }

    // 3) Windows: PowerShell (WorkingSet64 in bytes)
    try {
      var out = runAndRead("powershell", "-NoProfile", "-Command",
          "(Get-Process -Id " + pid + ").WorkingSet64");
      if (!out.isBlank()) return Long.parseLong(out.trim());
    } catch (Exception ignore) { /* last resort */ }

    throw new IllegalStateException("Nu pot determina RSS (OS-level) pentru acest sistem.");
  }

  static String runAndRead(String... cmd) throws Exception {
    var pb = new ProcessBuilder(cmd).redirectErrorStream(true);
    var p = pb.start();
    try (var in = p.getInputStream()) {
      var s = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
      p.waitFor();
      return s;
    }
  }

  public static String human(long bytes) {
    double v = bytes;
    String[] u = {"B","KB","MB","GB","TB"};
    int i = 0;
    while (v >= 1024 && i < u.length - 1) { v /= 1024; i++; }
    return String.format(java.util.Locale.ROOT, "%.2f %s", v, u[i]);
  }

  public static String formatSize(long usedHeapBytes) {
    return String.format("%,d B", usedHeapBytes);
  }

  public static long getUsedHeapBytes() {
    System.gc();
    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
  }
  public static long getTotalHeapBytes() {
    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted();
  }

  public static void onEnterExit() {
    new Thread(() -> {
      new Scanner(System.in).nextLine();
      System.out.println("ENTER detected. System.exit(0);");
      System.exit(0);
    }).start();
  }

  public static String objectToString(Object x) {
    return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
  }

  public static String done() {
    return " ✔ " + LocalTime.now().format(ofPattern("hh:mm:ss.S"));
  }

  /** @return in bytes */
  public static int MB(int mb) {
    return mb * 1024 * 1024;
  }

  /** @return in bytes */
  public static int KB(int kb) {
    return kb * 1024;
  }

  @SneakyThrows
  public static String getJavacVersion(Class<?> clazz) {
    Map<Integer, String> JAVA_CLASS_VERSION_MAP = Map.of(
        52, "8",
        55, "11",
        61, "17",
        65, "21",
        69, "25"
    );
    String resource = "/" + clazz.getName().replace('.', '/') + ".class";
    try (InputStream in = clazz.getResourceAsStream(resource)) {
      DataInputStream dis = new DataInputStream(in);
      dis.readInt();  // 0xCAFEBABE
      int minor = dis.readUnsignedShort(); // skipped
      int major = dis.readUnsignedShort();
      return JAVA_CLASS_VERSION_MAP.getOrDefault(major, "non-LTS:"+major);
    }
  }


}
