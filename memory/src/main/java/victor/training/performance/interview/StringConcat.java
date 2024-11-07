package victor.training.performance.interview;

public class StringConcat {
  public String method(String s, int i, String p) {
//    return s + i + p;
    // String csvLine = "";
    // csvLine += s + ";";
    // csvLine += s + ";";
    // csvLine += s + ";";
    // csvLine += s + ";";

    // micro-optimizations done without measuring the benefit.
    // "Guessing" = bad:
//    return new StringBuilder().append(s).append(i).append(p).toString();

    return s + i + p;
  }
}
