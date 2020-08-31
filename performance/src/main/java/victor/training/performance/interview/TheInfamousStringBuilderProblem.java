package victor.training.performance.interview;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {

      List<String> elements = IntStream.range(1,1_000_000).boxed().map(String::valueOf).collect(Collectors.toList());

      long t0 = System.currentTimeMillis();
      try (Writer writer = new BufferedWriter(new FileWriter(new File("out.txt")))) {
         concatenateAllAndWrite(elements.stream(), writer);
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1-t0));
   }

   private static void concatenateAllAndWrite(Stream<String> elementStream, Writer writer) {
      elementStream.forEach(e -> {
         try {
            writer.write(e);
            writer.write(",");
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      });
//      String result = "";
//      for (String element : elements) {
//         result += element + ",";
//      }
//      return result;
   }
}
