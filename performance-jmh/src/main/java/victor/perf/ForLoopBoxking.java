package victor.perf;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.Unchecked;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class ForLoopBoxking {

   private long[] listArr;
   //this takes 2x more memory that the long[]
   private Long[] listArr2;

   //worst case, lista de mai jos ocupa = + 25% memorie
   private List<Long> list;

   @Setup
   public void initData() {
      list = LongStream.range(1, 10_000_000)
          .boxed().collect(toList());
      listArr = list.stream().mapToLong(l -> l).toArray();
   }

   @Benchmark
   public long sumaUnrolleed() {
      long sum = 0L;
      for (int i = 0; i < list.size() / 4; i++) {
         sum += list.get(i * 4);
         sum += list.get(i * 4 + 1);
         sum += list.get(i * 4 + 2);
         sum += list.get(i * 4 + 3);
      }
      return sum;
   }
   @Benchmark
   public long sumaLongPrimitives() {
      long sum = 0L;
      for (long l : listArr) {
         sum += l;
      }
      return sum;
   }

   @Benchmark
   public long sumaBruta() {
      long sum = 0L;
      for (Long e : list) {
         sum += e;
      }
      return sum;
   }


}

