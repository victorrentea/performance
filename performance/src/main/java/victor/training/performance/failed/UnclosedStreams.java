package victor.training.performance.failed;

import victor.training.performance.PerformanceUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class UnclosedStreams {
   public static void main(String[] args) throws IOException {
      List<Path> allJavaFiles = Files.find(new File(".").toPath(), 999,
          (p, bfa) -> bfa.isRegularFile() && p.toFile().getAbsolutePath().endsWith(".java"))
          .collect(Collectors.toList());

      int sum=0;
      for (int i = 0; i < 1000; i++) {
         for (Path java : allJavaFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(java.toFile()));
            sum += (reader.readLine()).length();
         }
      }

      System.out.println(sum);

      PerformanceUtil.waitForEnter();
   }
}
