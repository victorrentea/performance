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
   public int n_items;

   @Param({"light", "heavy"})
   public String cpu_intensity;

   @Benchmark
   public int forClassic() {
      int sum = 0;
      for (int i = 0; i < n_items; i++) {
         sum += cpuOnlyTask(i);
      }
      return sum;
   }

   @Benchmark
   public int stream() {
      return IntStream.range(0, n_items)
          .map(this::cpuOnlyTask)
          .sum();
   }

   @Benchmark
   public int streamParallel() {
      return IntStream.range(0, n_items)
          .parallel()
          .map(this::cpuOnlyTask)
          .sum();
   }

   public int cpuOnlyTask(int n) {
      switch (cpu_intensity) {
         case "light":
            return (int) sqrt(n);
         case "heavy":
            double sum = 0;
            for (int i = n * 50; i < (n + 1) * 50; i++) {
               sum += sqrt(i);
            }
            return (int) sum;
         default:
            throw new IllegalStateException("Unexpected value: " + cpu_intensity);
      }
   }

}
