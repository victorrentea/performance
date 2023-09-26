package victor.training.performance.parallelStream;

import static java.lang.System.currentTimeMillis;

public class NaiveBench {
  public static void main(String[] args) {

    long t0 = currentTimeMillis();
    for (int i = 0; i < 1000000000; i++) {
    }
    long t1 = currentTimeMillis();
    System.out.println(t1 - t0);
  }
}
