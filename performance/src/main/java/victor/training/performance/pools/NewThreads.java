package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class NewThreads {
   public static void main(String[] args) throws InterruptedException {
      Thread t = new Thread(new Runnable() {
         @Override
         public void run() {
            log.debug("Hop si io");
//            Thread.currentThread().isInterrupted()
            new Scanner(System.in).nextLine();
//            reader.read()
         }
      });
      t.start();
      Thread.sleep(200);
      t.interrupt();
//      wait()

   }
}
