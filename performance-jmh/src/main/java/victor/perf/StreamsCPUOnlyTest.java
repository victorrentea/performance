package victor.perf;

import org.openjdk.jmh.annotations.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import rx.Observable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.toList;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 15, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StreamsCPUOnlyTest {


   @Param({"100", "10000"})
   public int n_items;

   @Param({"light", "heavy"})
   public String cpu_intensity;

   private List<Integer> numbers;

   @Setup
   public void createNumbersList() {
      numbers = IntStream.range(0, n_items).boxed().collect(toList());
   }

   @Benchmark
   public int forClassic() {
      int sum = 0;
      for (int n : numbers) {
         sum += cpuOnlyTask(n);
      }
      return sum;
   }

   @Benchmark
   public long stream() {
      return numbers.stream()
          .map(this::cpuOnlyTask)
          .count();
   }

   @Benchmark
   public long streamParallel() {
      return numbers.parallelStream()
          .map(this::cpuOnlyTask)
          .count();
   }

   //   @Benchmark
   public Long fluxSingleThread() {
      return Flux.fromIterable(numbers)
          .map(this::cpuOnlyTask)
          .count()
          .block();
   }

   //   @Benchmark
   public Long fluxParallelThread() {
      return Flux.fromIterable(numbers)
          .parallel()
          .runOn(Schedulers.parallel())
          .map(this::cpuOnlyTask)
          .sequential()
          .count()
          .block();
   }

   //   @Benchmark
   public Integer observableSingleThread() {
      return Observable.from(numbers)
          .map(this::cpuOnlyTask)
          .count()
          .toSingle()
          .toBlocking()
          .value();
   }

   public int cpuOnlyTask(int n) {
//      System.out.println(Thread.currentThread().getName());
      switch (cpu_intensity) {
         case "light":
            return (int) sqrt(n);
         case "heavy":
            double sum = 0;
            for (int i = n * 500; i < (n + 1) * 500; i++) {
               sum += sqrt(i);
            }
            return (int) sum;
         default:
            throw new IllegalStateException("Unexpected value: " + cpu_intensity);
      }
   }


//   public static void main(String[] args) {
//      new StreamsCPUOnlyTest().m();
//   }
//   public void  m() {
//      n_items=10000;
//      createNumbersList();
//      cpu_intensity = "heavy";
//      System.out.println(Flux.fromIterable(numbers)
//          .parallel()
//          .runOn(Schedulers.parallel())
//          .map(this::cpuOnlyTask)
//          .sequential()
//          .count()
//          .block());
//   }
}
