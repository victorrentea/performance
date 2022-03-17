package victor.training.performance.concurrency;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import static victor.training.performance.util.PerformanceUtil.sleepNanos;

@Slf4j
public class DeadLocks {
   @Value
   static class Fork {
      int id;
   }

   static class Philosopher extends Thread {
      private final Fork leftFork;
      private final Fork rightFork;

      public Philosopher(String name, Fork leftFork, Fork rightFork) {
         super(name);
         this.leftFork = leftFork;
         this.rightFork = rightFork;
      }

      public void run() {
         Fork firstFork = leftFork;
         Fork secondFork = rightFork;

         for (int i = 0; i < 5000; i++) {
            log.debug("I'm hungry");

            log.debug("Waiting for first fork (id=" + firstFork.id + ")...");
            synchronized (firstFork) {
               log.debug("Took the first fork");
               log.debug("Waiting for the second fork (id=" + secondFork.id + ")...");
               synchronized (secondFork) {
                  log.debug("Took both forks. Eating 🌟🌟🌟 ...");
                  sleepNanos(10);
                  log.debug("Done eating. Releasing forks");
               }
            }
            log.debug("Thinking...");
            sleepNanos(10);
         }
         log.debug("NORMAL FINISH (no deadlock happened)");
      }

   }

   public static void main(String[] args) {
      log.debug("Start");
      Fork[] forks = {new Fork(1), new Fork(2), new Fork(3), new Fork(4), new Fork(5)};
      new Philosopher("Plato", forks[0], forks[1]).start();
      sleepNanos(1);
      new Philosopher("Confucius", forks[1], forks[2]).start();
      sleepNanos(1);
      new Philosopher("Socrates", forks[2], forks[3]).start();
      sleepNanos(1);
      new Philosopher("Voltaire", forks[3], forks[4]).start();
      sleepNanos(1);
      new Philosopher("Descartes", forks[4], forks[0]).start();
   }
}
