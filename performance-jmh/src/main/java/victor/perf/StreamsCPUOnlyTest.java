package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 30, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StreamsCPUOnlyTest {


   @Param({"100", "10000"})
   public int N;

   @Param({"light", "heavy"})
   public String cpuIntensity;

//   public static final int N = 10_000;

   @Benchmark
   public int forClassic() {
      int sum = 0;
      for (int i = 0; i < N; i++) {
         sum += cpuOnlyTask(i);
      }
      return sum;
   }

   @Benchmark
   public int stream() {
      return IntStream.range(0, N)
          .map(this::cpuOnlyTask)
          .sum();
   }

   @Benchmark
   public int streamParallel() {
      return IntStream.range(0, N)
          .parallel()
          .map(this::cpuOnlyTask)
          .sum();
   }

   public int cpuOnlyTask(int n) {
      switch (cpuIntensity) {
         case "trivial":
            return (int) (n * n);
         case "light":
            return (int) sqrt(n);
         case "heavy":
            double sum = 0;
            for (int i = n * 50; i < (n + 1) * 50; i++) {
               sum += sqrt(i);
            }
            return (int) sum;
         default:
            throw new IllegalStateException("Unexpected value: " + cpuIntensity);
      }
   }

}
