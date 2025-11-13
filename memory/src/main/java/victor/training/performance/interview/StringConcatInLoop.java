package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();

      List<String> elements = IntStream.range(1, 50_000)
          .mapToObj(n -> "hahaha") // 6 x 2 bytes/char = 12 bytes / element
          .toList();

      System.out.println("Start...");
      long t0 = System.currentTimeMillis();

      // TODO: OPINIONS about this code ?
     try (FileWriter fileWriter = new FileWriter("out.txt")) {
       for (String element : elements) {
         fileWriter.write(element);
       }
     }

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
   }
}
