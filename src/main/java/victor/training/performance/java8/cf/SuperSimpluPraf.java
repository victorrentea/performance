package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.PerformanceUtil;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class SuperSimpluPraf {
    private static final Logger log = LoggerFactory.getLogger(SuperSimpluPraf.class);
    public static void main(String[] args) {
        CompletableFuture.runAsync(() -> {
            PerformanceUtil.sleepMillis(1000);
            System.out.println(Thread.currentThread().isDaemon() + " cu numele ");
            log.info("HAppy halloween!"); // chestia asta ruleaza pe un thread din
//             ForkJoinPool.commonPool()// din JVM care are threaduri daemon
        });
        log.info("A iesit main");
        PerformanceUtil.sleepMillis(2000); // fara asta, nu ramane in viata JVM pentru a printa Happy Hall..
//        new Scanner(System.in); // asta asteapta ENTER
    }
}
