package victor.training.performance;

import lombok.Value;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepq;

public class DeadLocks {
   @Value
   static class Fork {
      int id;
   }
//   private static final  Object monitor; //1
//   private static final  Object monitor; //2
//   private static final  Object monitor; //3



   static class Philosopher extends Thread {
      private final Fork leftFork;
      private final Fork rightFork;

      public Philosopher(String name, Fork leftFork, Fork rightFork) {
         super(name);
         this.leftFork = leftFork;
         this.rightFork = rightFork;
      }

      public void run() {
         Fork firstFork = leftFork.id < rightFork.id? leftFork: rightFork;
         Fork secondFork = leftFork.id > rightFork.id? leftFork:rightFork;

         for (int i = 0; i < 5000; i++) {
            log("I want to eat!");

            log("Waiting for first fork (id=" + firstFork.id + ")");
            synchronized (firstFork) {
               log("Took the first");
               log("Taking second fork (id=" + secondFork.id + ")");
               synchronized (secondFork) {
                  log("Took the second");
                  log("Took both forks. Eating...");
                  eat();
                  log("I had enough. I'm putting down the forks");
               }
            }
            log("Put down forks. Thinking...");
         }
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
