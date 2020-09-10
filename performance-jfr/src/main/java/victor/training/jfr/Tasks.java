package victor.training.jfr;

public class Tasks {
   public static int cpu(int millis) {
      long t0 = System.currentTimeMillis();
      int sum = 0;
      while (System.currentTimeMillis() - t0 < millis) {
         sum += Math.sqrt(System.currentTimeMillis());
      }
      return sum;
   }
}
