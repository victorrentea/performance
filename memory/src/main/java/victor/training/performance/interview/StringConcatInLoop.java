package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StringConcatInLoop {
   public static void main(String[] args) throws IOException {
      PerformanceUtil.printJfrFile();

//      List<String> elements = fromDb() // 6 x 2 bytes/char = 12 bytes / element
//          .toList();

      System.out.println("Start...");
      long t0 = System.currentTimeMillis();


      // TODO: OPINIONS about this code ?
     try (FileWriter fileWriter = new FileWriter("out.txt")) {
       fromDb().forEach(element-> {
         try {
           fileWriter.write(element);
         } catch (IOException e) {
           throw new RuntimeException(e);
         }
       });
//       for (String element : elements) {
//         fileWriter.write(element);
//       }
     }

      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
   }

  private static Stream<String> fromDb() {
    return IntStream.range(1, 50_000)
        .mapToObj(n -> "hahaha");
  }
}
