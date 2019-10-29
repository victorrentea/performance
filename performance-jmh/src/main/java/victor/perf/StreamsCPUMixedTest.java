package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StreamsCPUMixedTest {


	private static final int PARALLEL_REQUESTS_COUNT = 10; // try 4, 10
	public static final int N = 20;
	public static final int IO_TO_CPU_RATIO = 1; // try 1, 2, 5

	private ExecutorService pool;
	@Setup
	public void startThreadPool() {
		pool = Executors.newFixedThreadPool(PARALLEL_REQUESTS_COUNT);
	}
	@TearDown
	public void closeThreadPool() throws InterruptedException {
		pool.shutdownNow();
		pool.awaitTermination(1,TimeUnit.SECONDS);
	}

	private int acc;

	@Benchmark
	public double forClassic() throws ExecutionException, InterruptedException {
		Callable<Double> task = () -> {
			double localSum = 0;
			for (int i = 0; i < N; i++) {
				localSum += mixedTask();
			}
			return localSum;
		};
		return runInParallel(task);
	}
	@Benchmark
	public double streamSerial() throws ExecutionException, InterruptedException {
		Callable<Double> task = () -> IntStream.range(0, N).mapToDouble(i -> mixedTask()).sum();
		return runInParallel(task);
	}
	@Benchmark
	public double streamParallel() throws ExecutionException, InterruptedException {
		Callable<Double> task = () -> IntStream.range(0, N).parallel().mapToDouble(i -> mixedTask()).sum();
		return runInParallel(task);
	}

	private double runInParallel(Callable<Double> task) throws InterruptedException, ExecutionException {
		List<Future<Double>> futureList = new ArrayList<>();
		for (int i = 0; i < PARALLEL_REQUESTS_COUNT; i++) {
			futureList.add(pool.submit(task));
		}
		// all started
		double totalSum = 0;
		for (Future<Double> future : futureList) {
			totalSum += future.get();
		}
		return totalSum;
	}


	public double mixedTask() {
		try {
			Thread.sleep(IO_TO_CPU_RATIO); // 1 ms IO
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return oneMilliCpuTask(); // 1 ms CPU
	}
	@Benchmark //uncomment to prove it takes 1 milli
	public double oneMilliCpuTask() {
		double sum = 0;
		for (int i = 0; i < 450_000; i++) {
			sum += Math.sqrt(i);
		}
		return sum;
	}




}
