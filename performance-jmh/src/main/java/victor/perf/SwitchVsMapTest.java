//package victor.perf;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
//@State(Scope.Thread)
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.NANOSECONDS)
//@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
//@Fork(1)
//public class SwitchVsMapTest {
//
//   private static final Map<String, Integer> MAP = new HashMap<>(); // 10 keys
//	static {
//		MAP.put("0", 0);
//		MAP.put("1", 1);
//		MAP.put("2", 2);
//		MAP.put("3", 3);
//		MAP.put("4", 4);
//		MAP.put("5", 5);
//		MAP.put("6", 6);
//		MAP.put("7", 7);
//		MAP.put("8", 8);
//		MAP.put("9", 9);
//	}
//	public static String KEY = "3";
//
//   @Benchmark
//   public int map() throws ExecutionException, InterruptedException {
//      return MAP.get(KEY);
//   }
//
//   @Benchmark
//   public double oldSwitch() throws ExecutionException, InterruptedException {
//      switch (KEY) {
//			case "0": return 0;
//			case "1": return 1;
//			case "2": return 2;
//			case "3": return 3;
//			case "4": return 4;
//			case "5": return 5;
//			case "6": return 6;
//			case "7": return 7;
//			case "8": return 8;
//			case "9": return 9;
//      }
//      return -1;
//   }
//
//
//}
