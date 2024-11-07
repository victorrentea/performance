package victor.training.performance.interview;

import java.util.List;
import java.util.stream.IntStream;

public class StreamsVsFor {
  public static void main(String[] args) {
    List<Integer> numbers = IntStream.range(0, 10_000_000).boxed().toList();

    long start = System.currentTimeMillis();
    long sum = 0;
    for (int element : numbers) {
      sum += element;
    }
    System.out.println("For: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Sum: " + sum);

    start = System.currentTimeMillis();
    sum = numbers.stream().mapToInt(i -> i).sum();
    System.out.println("Stream: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Sum: " + sum);
  }
}
