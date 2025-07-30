package victor.training.performance.interview;

import org.jooq.lambda.Unchecked;
import victor.training.performance.util.PerformanceUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
  public static void main(String[] args) throws IOException {
    PerformanceUtil.printJfrFile();

    Stream<String> elements = repoFind(); // iterate through the results of an  SQL,Mongo query or file lines

    waitForEnter();
    System.out.println("Start...");
    long t0 = System.currentTimeMillis();

    // TODO: OPINIONS about this code ?
    try (Writer fw = new BufferedWriter(new FileWriter("out.txt"))) {
      elements.forEach(Unchecked.consumer(e -> fw.write(e)));
    }

    System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
    waitForEnter();
  }

  private static Stream<String> repoFind() {
    return IntStream.range(1, 1000_000)
        .mapToObj(n -> "hahaha");// 6 x 2 bytes/char = 12 bytes / element
  }
}
