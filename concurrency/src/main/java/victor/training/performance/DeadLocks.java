package victor.training.performance;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.log;

@Slf4j
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
        log("I'm hungry");

//        synchronized (firstFork) { // 'syncrhonized' word is bad since java 21 (~ Virtual Threads don't like this)

        log("Taking fork #" + firstFork.id + "...");
        firstFork.lock.lock();
        try {
          log("Took one fork");
          log("Taking fork #" + secondFork.id + "...");
          secondFork.lock.lock();
          try {
            log("Eating🌟...");
            log("Done eating. Releasing forks");
          } finally {
            secondFork.lock.unlock();
          }
        } finally {
          firstFork.lock.unlock();
        }
        log("Thinking...");
      }
      log("NORMAL FINISH (no deadlock happened)");
    }
  }

  public static void main(String[] args) {
    Thread t = new Thread(() -> {
      PerformanceUtil.sleepMillis(5_000);
      long pid = ProcessHandle.current().pid();
      System.out.println("To see a thread dump run in terminal one of:\njstack " + pid + "\nkill -3 " + pid);
    });
    t.setDaemon(true); // allow the process to die
    t.start();

    DeadLocks.log.debug("Start");
    DeadLocks.Fork[] forks = {new Fork(1), new Fork(2), new Fork(3), new Fork(4), new Fork(5)};
    new DeadLocks.Philosopher("Plato", forks[0], forks[1]).start();
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
