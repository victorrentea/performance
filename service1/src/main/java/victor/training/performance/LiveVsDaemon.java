package victor.training.performance;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class LiveVsDaemon {
  public static void main(String[] args) {
    new Thread(()->{
      try {
        sleep(3000);
       System.out.println("Ready");

          } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }).start();

//    CompletableFuture.runAsync(() -> {
//      try {
//        sleep(3000);
//        System.out.println("Ready");
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//    });
  }
}
