package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();

      List<String> elements = IntStream.range(1, 50_000)
          .mapToObj(n -> "hahaha") // 6 x 2 bytes/char = 12 bytes / element
          .toList();

      System.out.println("Start...");
      long t0 = System.currentTimeMillis();

      // TODO: OPINIONS about this code ?
      StringBuilder s = new StringBuilder(); // is what ArrayList<Integer> is to Integer[] ~unused capacity
      for (String element : elements) {
         s.append(element); // less malloc
      }





      Files.writeString(new File("out.txt").toPath(), s.toString(), UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
   }
}
