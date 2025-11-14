package victor.training.performance.interview;


import java.util.List;
import java.util.stream.LongStream;

public class SummingBoxes {

  public static void main(String[] args) {
    List<Long> list = LongStream.range(1, 40_000_000)
        .boxed()
        .toList();

    long t0 = System.currentTimeMillis();

    Long sum = sumThemUp(list);

    long t1 = System.currentTimeMillis();
    System.out.println("sum = " + sum);
    System.out.println((t1 - t0) + " ms");
  }

  private static Long sumThemUp(List<Long> list) {
    // TODO Reduce run time by changing 1 character below 😊
    long sum = 0L;
    for (Long i : list) {
      sum += i;
    }
    return sum;
  }
}
