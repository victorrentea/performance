package victor.training.performance.leak;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class NewLeakWannabe {
  public static void main(String[] args) throws IOException {

//    InputStream inputStream = returningInputStream();
    try ( InputStream inputStream = returningInputStream()) {
      System.out.println(inputStream.read());
    }
  }

  private static InputStream returningInputStream() {
    return getReader()
        .map(slothReader -> slothReader.getInputStream()) // *might* create an input stream
        .filter(is -> Math.random() < .5)
        .orElse(null);
  }

  private static Optional<SlothReader> getReader() {
    if (Math.random() < .5)
      return Optional.of(new SlothReader());
    else
      return Optional.empty();

  }

  public record SlothReader() {
    public InputStream getInputStream() {
      return new ByteArrayInputStream("data".getBytes());
    }
  }

}
