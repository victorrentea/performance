package victor.training.performance.interview;

import org.apache.commons.io.FileUtils;
import victor.training.performance.util.PerformanceUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.waitForEnter;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();
      List<String> elements = IntStream.range(1, 50_000)
          .mapToObj(n -> "hahaha") // 6 ch x 2 bytes = 12
          .collect(toList());

      waitForEnter();
      System.out.println("Start writing contents to file!");
      long t0 = System.currentTimeMillis();

      //
//      BufferedReader reader = FileUtils.openInputStream(new File("in.txt"));
//      Stream<string> lines = reader.lines()
//      Stream<Entity> lines = repo.streamAll()

      try (FileWriter writer = new FileWriter("out.txt")) {
         for (String element : elements) {
            writer.write(element);
         }
      }
//      StringBuilder s = new StringBuilder();
//      for (String element : elements) {
//         s.append(element);
//      }
//      FileUtils.writeStringToFile(new File("out.txt"), s.toString(), UTF_8);

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      waitForEnter();
   }
}
