package victor.training.performance.pools;

import lombok.Value;
import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Canonical {

  public static final int ONE_MILLION = 1_000_000;

  public static void main(String[] args) {
    // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
    long heap0 = PerformanceUtil.getUsedHeapBytes();

    List<Long> rawIds = LongStream.range(0, ONE_MILLION)
            .map(n -> n % 10)
            .boxed()
            .collect(Collectors.toList());

    long heap1 = PerformanceUtil.getUsedHeapBytes();

    List<MyId> microtypes = LongStream.range(0, ONE_MILLION)
            .map(n -> n % 10)
            .mapToObj(MyId::new)
            .collect(Collectors.toList());

    long heap2 = PerformanceUtil.getUsedHeapBytes();
    System.out.println("Numbers list takes: " + (heap1 - heap0) / 1024 / 1024 + " MB " + PerformanceUtil.objectToString(rawIds));
    System.out.println("Microtypes list takes: " + (heap2 - heap1) / 1024 / 1024 + " MB " + PerformanceUtil.objectToString(microtypes));
  }


}

@Value
class MyId {
  //  private static final WeakHashMap<MyId, MyId>
  long val;

}
