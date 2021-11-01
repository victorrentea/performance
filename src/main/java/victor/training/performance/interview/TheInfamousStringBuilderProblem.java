package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      List<String> elements = IntStream.range(1, 50_000) // try 50K x 6 chars : see TLAB
          .mapToObj(n -> "hahaha")
          .collect(toList());

      PerformanceUtil.waitForEnter();
      long t0 = System.currentTimeMillis();

      try (FileWriter writer = new FileWriter("out.txt")) { // sau pe CLOB sau pe HttpServletResponse.getWriter()

//         Stream<String> lines = Files.lines(new File("out.txt").toPath());
         evident(elements.stream(), writer);
      }


      System.out.println("Done. Took " + (System.currentTimeMillis() - t0));
      PerformanceUtil.waitForEnter();
   }

   private static void evident(Stream<String> elements , FileWriter writer) throws IOException {
//      for (String element : elements) {
//         writer.write(element);
         elements.forEach(e -> {
            try {
               writer.write(e);
            } catch (IOException ex) {
               throw new RuntimeException(ex);
            }
         });
//      }
   }
}


// char *s
// int*