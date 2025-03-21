package victor.training.performance.interview;

import org.jooq.lambda.Unchecked;
import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
  public static void main(String[] args) throws IOException {
    PerformanceUtil.printJfrFile();

    Stream<String> elements = IntStream.range(1, 50_000)
        .mapToObj(n -> "hahaha") // 6 x 2 bytes/char = 12 bytes / element
        ;

    waitForEnter();
    System.out.println("Start...");
    long t0 = System.currentTimeMillis();

    stupid(elements);
    String sss = "Done. Took ";
    sss = null;
    //=-----
    System.out.println(sss + (System.currentTimeMillis() - t0));
    waitForEnter();
  }

  private static void stupid(Stream<String> elements) throws IOException {
    // TODO: OPINIONS about this code ?
    try (FileWriter fos = new FileWriter("out.txt")) {
      elements
          .map(String::toUpperCase)
          .forEach(Unchecked.consumer(fos::write));
    }
  }
}
