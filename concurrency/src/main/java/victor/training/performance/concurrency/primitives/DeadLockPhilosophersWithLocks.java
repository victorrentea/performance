package victor.training.performance.concurrency.primitives;

import lombok.SneakyThrows;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.locks.ReentrantLock;


public class DeadLockPhilosophersWithLocks {
  static class Philosopher extends Thread {
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;

    public Philosopher(String name, int leftId, ReentrantLock leftFork, int rightId, ReentrantLock rightFork) {
      super(name);
      this.leftFork = leftFork;
      this.rightFork = rightFork;
    }

    @SneakyThrows
    public void run() {

      for (int i = 0; i < 50; i++) {
//        PerformanceUtil.sleepSomeTime();
        PerformanceUtil.log("I'm hungry!");

        PerformanceUtil.log("Waiting for first fork");
//        if (!leftFork.tryLock(50, TimeUnit.MILLISECONDS)) continue; // Redis.lock
        leftFork.lock();
        try {
          PerformanceUtil.log("Took it");
          PerformanceUtil.sleepSomeTime();
          PerformanceUtil.log("Taking second fork");
//          if (!rightFork.tryLock(50, TimeUnit.MILLISECONDS)) continue;
          rightFork.lock();
          try {

            eat();

          } finally {
            rightFork.unlock();
          }
//          PerformanceUtil.sleepSomeTime();
        } finally {
          leftFork.unlock();
        }
        PerformanceUtil.log("Put down forks. Thinking...");
      }
    }

    private void eat() {
      PerformanceUtil.log("Took both forks. Eating...");
      PerformanceUtil.sleepSomeTime();
      PerformanceUtil.log("I had enough. I'm putting down the forks");
    }
  }

  public static void main(String[] args) {
    PerformanceUtil.log("Start");
    ReentrantLock[] forks = new ReentrantLock[]{new ReentrantLock(), new ReentrantLock(), new ReentrantLock(), new ReentrantLock(), new ReentrantLock()};
    new Philosopher("Plato", 1, forks[0], 2, forks[1]).start();
    new Philosopher("Konfuzius", 2, forks[1], 3, forks[2]).start();
    new Philosopher("Socrates", 3, forks[2], 4, forks[3]).start();
    new Philosopher("Voltaire", 4, forks[3], 5, forks[4]).start();
    PerformanceUtil.sleepMillis(1000);
    new Philosopher("Descartes", 5, forks[4], 1, forks[0]).start();
  }
}
