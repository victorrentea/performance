package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.log;

@Slf4j
public class DeadLocks {
   record Fork(int id, ReentrantLock lock) {
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
         Fork firstFork = leftFork; //< rightFork.id ? leftFork : rightFork;
         Fork secondFork = rightFork; //< rightFork.id ? rightFork : leftFork;

         for (int i = 0; i < 5000; i++) {
            log("I'm hungry");

            log("Taking " + firstFork + "...");
//            synchronized (firstFork) {
            firstFork.lock.lock();
            try {
               log("Taking " + secondFork + "...");
//               synchronized (secondFork) {
               secondFork.lock.lock();
               try {
                  log("Eating🌟...");
                  // sleepNanos(10);
                  log("Done. Releasing forks");
               } finally {
                  secondFork.lock.unlock();
               }
            } finally {
               firstFork.lock.unlock();
            }
            log("Thinking...");
         }
         log("✅NORMAL FINISH (no deadlock happened)");
      }
   }

   public static void main(String[] args) {
      DeadLocks.log.debug("Start");
      Fork[] forks = {
          new Fork(1,new ReentrantLock()),
          new Fork(2,new ReentrantLock()),
          new Fork(3,new ReentrantLock()),
          new Fork(4,new ReentrantLock()),
          new Fork(5,new ReentrantLock())}
          ;
      new Philosopher("Plato", forks[0], forks[1]).start();
      PerformanceUtil.sleepNanos(1);
      new Philosopher("Confucius", forks[1], forks[2]).start();
      PerformanceUtil.sleepNanos(1);
      new Philosopher("Socrates", forks[2], forks[3]).start();
      PerformanceUtil.sleepNanos(1);
      new Philosopher("Voltaire", forks[3], forks[4]).start();
      PerformanceUtil.sleepNanos(1);
      new Philosopher("Descartes", forks[4], forks[0]).start();
   }
}
