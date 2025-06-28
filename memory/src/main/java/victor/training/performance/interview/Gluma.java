package victor.training.performance.interview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Gluma {
  public static void main(String[] args) throws IOException {
    String a="127";
    String b="127"; // String Pool

    System.out.println(a==b);
  }
}
