package victor.training.performance;

import lombok.Value;

import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepq;

public class DeadLocks {
   @Value
   static class Fork {
      int id;
      ReentrantLock lock = new ReentrantLock();
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
            log("I want to eat!");

            log("Waiting for first fork (id=" + firstFork.id + ")");
//            synchronized (firstFork) {
            firstFork.lock.lock();
            try {
               log("Took the first");
               log("Taking second fork (id=" + secondFork.id + ")");
               if (secondFork.lock.tryLock()) {
                  try {
//               synchronized (secondFork) {
                     log("Took the second");
                     log("Took both forks. Eating...");
                     eat();
                     log("I had enough. I'm putting down the forks");
                  }finally {
                     secondFork.lock.unlock();
                  }
               }
            } finally {
               firstFork.lock.unlock();
            }
//            }
            log("Put down forks. Thinking...");
         }
         log("END");
      }

      private void eat() {
         // stuff
      }
   }

   public static void main(String[] args) {
      log("Start");
      Fork[] forks = {new Fork(1), new Fork(2), new Fork(3), new Fork(4), new Fork(5)};
      new Philosopher("Plato", forks[0], forks[1]).start();
      new Philosopher("Confucius", forks[1], forks[2]).start();
      new Philosopher("Socrates", forks[2], forks[3]).start();
      new Philosopher("Voltaire", forks[3], forks[4]).start();
      sleepq(1000);
      new Philosopher("Descartes", forks[4], forks[0]).start();
   }
}
