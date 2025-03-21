package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();

      List<String> elements = IntStream.range(1, 50_000)
          .mapToObj(n -> "hahaha") // 6 x 2 bytes/char = 12 bytes / element
          .toList();

      waitForEnter();
      System.out.println("Start...");
      long t0 = System.currentTimeMillis();

      // TODO: OPINIONS about this code ?
      String s = "";
      for (String element : elements) {
         s += element;
      }

      Files.writeString(new File("out.txt").toPath(), s, UTF_8);

      String sss = "Done. Took ";
      sss=null;
      //=-----
      System.out.println(sss + (System.currentTimeMillis() - t0));
      waitForEnter();
   }
}
