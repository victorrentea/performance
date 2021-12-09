package victor.training.performance.primitives;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadStop {

   public static void main(String[] args) throws InterruptedException {
      MyTask myTask = new MyTask();

      Thread t = new Thread(myTask);
      t.start();

      sleepSomeTime(2000, 3000);
      log("Trying to stop the thread");

//		myTask.running = false;
      t.interrupt();

//		Future<Integer> f;
//		f.cancel(true);
      // TODO gracefully stop the thread
      // TODO force the .wait() to interrupt

      log("Waiting for thread to finish...");
      t.join();
      log("Stopped");
   }

   static class MyTask implements Runnable {
      private boolean running = true;

      public void run() {
         CompletableFuture<ResponseEntity<String>> cf = new AsyncRestTemplate()
             .getForEntity("http://localhost:9999/api/register-sheep", String.class)
				.completable();

         while (!Thread.currentThread().isInterrupted()) {
            try {
               if (cf.isDone()) {
                  System.out.println("Got data: " + cf.get());
               }
               Thread.sleep(10);
            } catch (InterruptedException | ExecutionException e) {
               cf.cancel(true);
            }
            return;
         }
      }
//		public void run() {
//			RestTemplate rest = new RestTemplate();
////			try {
////				while (!Thread.currentThread().isInterrupted()) {
//					log("Still alive, waiting...");
//			try {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//					throw new RuntimeException(e);
//				}
//
//			Thread.sleep(200);
////				Thread.interrupted() // also clears the flag
//			} catch (Exception e) {
//				//swallows
//			}
//					String data = rest.getForObject("http://localhost:9999/api/register-sheep", String.class);
//					log("Existed rest without Interrupted Ex");
////			if (Thread.currentThread().isInterrupted()) {
////				throw new InterruptedException();
////			}
//
////				}
//				log("Gracefully stopped execution");
////			} catch (InterruptedException e) {
////				log("Interrupted. Exiting");
////			}
//		}
   }

}
