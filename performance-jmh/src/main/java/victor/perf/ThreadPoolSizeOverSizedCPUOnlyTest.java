package victor.perf;

import org.jooq.lambda.Unchecked;
import org.openjdk.jmh.annotations.*;
import victor.perf.tasks.CPUTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class ThreadPoolSizeOverSizedCPUOnlyTest {

   private final ExecutorService executor = Executors.newFixedThreadPool(100);

   @Setup
   public void calibrateCPUTask() {
      new CPUTask(100);
   }

   @Benchmark
   public int doWork() {
      List<Future<Integer>> futures = new ArrayList<>();
      for (int i = 0; i < 200; i++) {
         Future<Integer> future = executor.submit(new CPUTask(100));
         futures.add(future);
      }
      return futures.stream().mapToInt(Unchecked.toIntFunction(Future::get)).sum();
   }
}
