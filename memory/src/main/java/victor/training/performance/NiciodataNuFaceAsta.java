package victor.training.performance;


import java.util.concurrent.CompletableFuture;

public class NiciodataNuFaceAsta {
  public static void main(String[] args) {
    CompletableFuture.runAsync(() -> {
      System.out.println("Hello from " + Thread.currentThread().getName());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("After");
    }); // completable future.runAsync runs by default on a daemon thread
    System.out.println("Bye from " + Thread.currentThread().getName());
  }
}

