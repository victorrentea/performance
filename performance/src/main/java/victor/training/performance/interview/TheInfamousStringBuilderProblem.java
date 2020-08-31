package victor.training.performance.interview;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {

      List<String> elements = IntStream.range(1,10_000).boxed().map(String::valueOf).collect(Collectors.toList());

      long t0 = System.currentTimeMillis();
//      try (Writer writer = new BufferedWriter(new FileWriter(new File("out.txt")))) {
//         concatenateAllAndWrite(elements.stream(), writer);
//      }
      String s = m(elements);
      try (Writer writer = new BufferedWriter(new FileWriter(new File("out.txt")))) {
         writer.write(s);
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1-t0));
   }

   private static String m(List<String> elements) {
      StringBuilder s = new StringBuilder();
      for (String element : elements) {
         s.append(element).append(",");
//         s = s + element + ",";
//         s = new StringBuilder(s).append(element).append(",").toString();
      }

      // Morala: daca vreodata concatenezi multe stringuri cu + intre ele (fara niic un for)
      // lasa-le cu +, ca in bytecode iese StringBuilder oricum din Java 8+
//      String csvLine = new StringBuilder().append(order.getId()).append(";").toString();
//      clsLine += order.getFirstName() + ";";
      return s.toString();
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
