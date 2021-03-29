package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class OptionalCost {

   private AtomicInteger counter = new AtomicInteger(0);

   @Benchmark
   public Integer optional() {
      return createOptional(counter.incrementAndGet()).orElse(-1);
   }

   private Optional<Integer> createOptional(Integer n) {
      if (n % 2 == 0) {
         return Optional.empty();
      } else {
         return Optional.of(n);
      }
   }
   @Benchmark
   public Integer nulls() {
      Integer n = createNull(counter.incrementAndGet());
      if (n == null) return -1;
      return n;
   }

   private Integer createNull(Integer n) {
      if (n % 2 == 0) {
         return null;
      } else {
         return n;
      }
   }

}



