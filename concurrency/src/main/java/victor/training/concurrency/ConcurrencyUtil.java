package victor.training.concurrency;

import org.jooq.lambda.Unchecked;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrencyUtil {
	static Random random = new Random();
	
	public static void sleepSomeTime() {
		sleepSomeTime(10,100);
	}
	
	public static void sleepSomeTime(int min, int max) {
		sleep2(randomInt(min, max));
	}
	
	public static int randomInt(int min, int max) {
		return min + random.nextInt(max-min);
	}
	
	public static void sleep2(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void useCPU(long millis) {
		long tEnd = System.currentTimeMillis() + millis;
		while (System.currentTimeMillis() < tEnd) {
			Math.sqrt(Math.random());
		}
	}

	private static AtomicInteger concurrentCalls = new AtomicInteger(0);
	public static void saturatableExternalSystem() {
		int concurrent = concurrentCalls.incrementAndGet();
		int delta = 1000;
		if (concurrent > 3) delta += (concurrent - 3) * 300;
		sleep2(delta);
		concurrentCalls.decrementAndGet();
	}
	public static int measureCall() {
		long t0 = System.currentTimeMillis();
		saturatableExternalSystem();
		long t1 = System.currentTimeMillis();
		return (int) (t1-t0);
	}

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(5);

		List<Future<Integer>> futures = IntStream.range(0, 10).mapToObj(i -> pool.submit(() -> measureCall())).collect(Collectors.toList());

		double avg = futures.stream().map(Unchecked.function(Future::get)).mapToInt(Integer::intValue).average().getAsDouble();
		System.out.println(avg);
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
			synchronized (ConcurrencyUtil.class) {
				pad = String.format("%" + (1+ position.size() * PAD_SIZE ) + "s", "");
				System.out.println(line + pad + threadName);
				System.out.println(line + pad + "=============");
				position.add(threadName);
			}
		}
		System.out.println(line + pad + message);
	}
}
