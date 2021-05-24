package victor.training.performance.jfr.jaxb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XmlGenerator {

   public static void generate(File file, int n) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
         writer.write("<records>");
         for (int i = 0; i < n; i++) {
            writer.write("<record><a>a" + i + "</a><b>b" + i + "</b><value>"+i+"</value></record>");
         }
         writer.write("</records>");
      } catch (IOException e) {
         throw new IllegalArgumentException(e);
      }
   }
}
