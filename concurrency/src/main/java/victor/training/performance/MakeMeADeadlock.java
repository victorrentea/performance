package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class MakeMeADeadlock {
  public static void main(String[] args) {
    Object lock1= new Object();
    Object lock2= new Object();

    CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
      synchronized (lock1) {
        sleepMillis(100);
        synchronized (lock2) {
          System.out.println("A");
        }
      }
    });
    CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
      synchronized (lock2) {
        log.info("What thread runs this? From what thread pool is this thread?");
        // Common ForkJoinPool.commonPool-worker/ JVM-wide ForkJoinPool global, shared
        // size = cores-1 = 9 on my machine
        sleepMillis(100);
        synchronized (lock1) {
          System.out.println("B");
        }
      }
    });
//    Thread.currentThread().isDaemon()
    // Java Threads can be  Daemon or User Threads.
    // a daemon thread does not stop JVM process from dying.
    // main just died here.
    // CF run their stuff on Daemon threads
    cf1.join();
    cf2.join();
//    ReentrantLock r;
//    r.trlo
  }
}


// how can we avoid deadlocks?
// 1. Don't use synchronized (locks: ReeentrantLock, ReadWriteLock)
// 2. use only ONE lock, not two or more
// 3. acquire all the locks you need in the same order.
// lock(account1), lock(account2), transfer 1->2
// lock(account2), lock(account1), transfer 1->2
// add an IF to tell which account you lock first> eg; sort by IBAN.

// 4. tryLock for a while, don;t wait forever
