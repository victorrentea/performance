package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StreamsCPUOnlyTest {


    public static final int N = 10_000;

    @Benchmark
	public int forClassic() {
		int sum = 0;
		for (int i = 0; i< N; i++) {
			sum += cpuOnlyTask(i);
		}
		return sum;
	}

	@Benchmark
	public int streamSerial() {
		return IntStream.range(0, N).map(this::cpuOnlyTask).sum();
	}

	@Benchmark
	public int streamParallel() {
		return IntStream.range(0, N).parallel().map(this::cpuOnlyTask).sum();
	}

	public int cpuOnlyTask(int n) {
//		return n * n;
		int sum = 0;
		for (int i = 0; i < 100; i++) {
			sum += Math.sqrt(n);
		}
		return sum;
	}

}
