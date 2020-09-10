package victor.training.jfr;

public class ThreadUtils {
   public static void sleepq(int millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
