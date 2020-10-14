//package victor.perf;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.concurrent.*;
//
//@State(Scope.Thread)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MICROSECONDS)
//@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
//@Fork(1)
//public class ExceptionsTest {
//
//
//	@Benchmark
//	public double newException() throws ExecutionException, InterruptedException {
//		try {
//			new IllegalArgumentException();
//		} catch (IllegalArgumentException e) {
//		}
//		return 1;
//	}
//	@Benchmark
//	public double exception() throws ExecutionException, InterruptedException {
//		try {
//			throw new IllegalArgumentException();
//		} catch (IllegalArgumentException e) {
//		}
//		return 1;
//	}
//	@Benchmark
//	public double deepException() throws ExecutionException, InterruptedException {
//		try {
//			return m1();
//		} catch (IllegalArgumentException e) {
//		}
//		return 1;
//	}
//
//	private double m1() {
//		return m2();
//	}
//
//	private double m2() {
//		return m3();
//	}
//	private double m3() {
//		return m4();
//	}
//	private double m4() {
//		return m5();
//	}
//	private double m5() {
//		return m6();
//	}
//	private double m6() {
//		return m7();
//	}
//	private double m7() {
//		return m8();
//	}
//	private double m8() {
//		return m9();
//	}
//
//	private double m9() {
//		throw new IllegalArgumentException();
//	}
//
//
//}
