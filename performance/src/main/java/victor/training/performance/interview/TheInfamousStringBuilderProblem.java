package victor.training.performance.interview;

import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      Stream<String> elements = IntStream.range(1, 30_000).mapToObj(String::valueOf);

      // TODO join elements with "," and print them on the console

      long t0 = System.currentTimeMillis();


      try (Writer writer = new FileWriter("a.txt")) {
         concatenate(elements, writer);
      }

      long t1 = System.currentTimeMillis();

      System.out.println(t1 - t0);

      // TODO 300K
      // TODO JFR +=
   }

   private static void concatenate(Stream<String> elements, Writer writer) throws IOException {
      elements.forEach(element ->
          {
             try {
                writer.write("a" + element + "\n");
             } catch (IOException e) {
                e.printStackTrace();
             }
          }
      );
//       String result= "";
//
//       for (String element : elements) {
//          result += element + "\n";
//
//       }
//       return result;
   }


}
