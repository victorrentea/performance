package victor.training.performance.cache;

import lombok.SneakyThrows;
import victor.training.performance.PerformanceUtil;

import java.io.FileReader;

public class Interrupted {
   static boolean running = true;

   public static void main(String[] args) {
      Thread thread = new Thread() {
         @SneakyThrows
         @Override
         public void run() {
            while (running) {
               // treaba
               boolean nu_am_primit_inca_date = true;
               while (nu_am_primit_inca_date) {
                  // http call
                  FileReader reader = new FileReader("a.txt");
                  int read = reader.read();
                  if (Thread.currentThread().isInterrupted()) {
                     throw new RuntimeException("Interrupted");
                  }
                  try {
                     Thread.sleep(1000);
                  } catch (InterruptedException e) {
//                     System.err.println(e);
                     throw new RuntimeException(e);
                  }
               }
            }
         }
      };
      running = false;
      thread.interrupt();
      PerformanceUtil.sleepq(1000);
      System.exit(-1);
   }
}
