package victor.training.performance.interview;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

public class TheInfamousStringBuilderProblem {
   public static void main(String[] args) throws IOException {
      Iterable<String> it = () -> {
         try {
            return Files.lines(new File("data.txt").toPath()).iterator();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      };
      mMare(it, new FileWriter("out.txt"));

   }

   public String m(List<String> linii) { // <50-100 elem nu conteaza
      StringBuilder s = new StringBuilder();
      for (String linie : linii) {
         s.append(linie).append("\n");
      }
      return s.toString();
   }

   // > 10K -> streamlining. Procesezi in chunkuri si versi outputul afara din RAM
   public static void mMare(Iterable<String> linii, Writer output) throws IOException { // <50-100 elem nu conteaza
      for (String linie : linii) {
         output.write(linie.toUpperCase()  + "\n");
      }
   }
}
