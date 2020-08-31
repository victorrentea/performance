package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class APlusPlusTest {

   public static int population;
   private static final Object MONITOR = new Object();

   @Benchmark
   public int newThread() throws InterruptedException { //1
      class TaskThread extends Thread {
         @Override
         public void run() {
            Integer localPopulation = f();
//            synchronized (MONITOR) {
               population+=localPopulation;
//            }
         }
      }
      Thread t1 = new TaskThread();
      Thread t2 = new TaskThread();
      t1.start();
      t2.start();
      t1.join();;
      t2.join();
      return population;
   }

   @Benchmark
   public int completableFuture() throws ExecutionException, InterruptedException { //2
      return supplyAsync(this::f)
          .thenCombineAsync(supplyAsync(this::f), Integer::sum)
          .get();
   }


   public Integer f() {
      int localPopulation = 0;
      for (int i = 0; i < 100_000; i++) {
         localPopulation++;
      }
      return localPopulation;
   }

   public int cpuOnlyTask(int n) {
//		return n * n;
      return (int) ((int) Math.log(Math.sqrt(n)) + Math.log(Math.sqrt(n)));
   }

   //


}
