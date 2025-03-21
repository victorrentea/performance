package victor.training.performance.leak;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class LoanPattern {
// v1
  public static InputStream openFile() throws FileNotFoundException {
    return new FileInputStream("data.xml");
  }
  public static void main(String[] args) throws IOException {
    //v1
    InputStream inputStream = openFile();
    work(inputStream);
    inputStream.close();
    // v2
    processFile(LoanPattern::work);
  }
  // v2 FP
  public static void processFile(Consumer<InputStream> fileProcessor) throws IOException {
    try (FileInputStream is = new FileInputStream("data.xml")) { // manage resouce
      fileProcessor.accept(is); // loan resource to the incoming behavior
    }
  }


  private static void work(InputStream inputStream)  {
    try {
      System.out.println(inputStream.read());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
