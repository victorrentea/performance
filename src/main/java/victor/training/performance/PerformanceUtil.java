package victor.training.performance;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class PerformanceUtil {
	static Random random = new Random();
	
	public static void sleepSomeTime() {
		sleepSomeTime(5,20);
	}
	
	public static void sleepSomeTime(int min, int max) {
		sleepq(randomInt(min, max));
	}
	
	public static int randomInt(int min, int max) {
		return min + random.nextInt(max-min);
	}

	/**
	 * Sleeps quietly (without throwing anything out)
	 */
	public static void sleepq(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void useCPU(long millis) {

	}


	public static int measureCall(Runnable r) {
		long t0 = System.currentTimeMillis();
		r.run();
		long t1 = System.currentTimeMillis();
		return (int) (t1-t0);
	}
	public static Callable<Integer> measuring(Runnable r) {
		return () -> measureCall(r);
	}


	
	static List<String> position = new ArrayList<>();
	public static void log(String message) {
		int PAD_SIZE = 20;
		String line = new SimpleDateFormat("hh:mm:ss.SSS").format(new Date()) + " ";
		String pad;
		String threadName = Thread.currentThread().getName();
		if (position.contains(threadName)) {
			pad = String.format("%" + (1+position.indexOf(threadName) * PAD_SIZE) + "s", "");
		} else {
			synchronized (PerformanceUtil.class) {
				pad = String.format("%" + (1+ position.size() * PAD_SIZE ) + "s", "");
				System.out.println(line + pad + threadName);
				System.out.println(line + pad + "=============");
				position.add(threadName);
			}
		}
		System.out.println(line + pad + message);
	}

	public static void waitForEnter() {
		System.out.println("[ENTER] to continue");
		new Scanner(System.in).nextLine();
		System.out.println("Continue...");
	}

	public static String getUsedHeap() {
		System.gc();
		return String.format("Used heap: %,d B", getUsedHeapBytes()).replace(",", " ");
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
