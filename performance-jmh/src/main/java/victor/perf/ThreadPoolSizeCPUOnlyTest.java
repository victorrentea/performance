package victor.perf;

import org.jooq.lambda.Unchecked;
import org.openjdk.jmh.annotations.*;
import victor.perf.tasks.CPUTask;

import java.io.InputStream;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class ThreadPoolSizeCPUOnlyTest {

   private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

   @Setup
   public void calibrateCPUTask() {
      new CPUTask(100);
   }
   @TearDown
   public void closeExecutor() throws InterruptedException {
      executor.shutdown();
     InputStream is;
     PreparedStatement preparedStatement;
     preparedStatement.executeUpdate()
      executor.awaitTermination(1, TimeUnit.SECONDS);
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
