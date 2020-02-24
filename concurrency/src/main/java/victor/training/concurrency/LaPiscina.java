package victor.training.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class LaPiscina {

    static ThreadLocal<String> userulDePeThread = ThreadLocal.withInitial(() -> null);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Random r = new Random();
        String user = "gigi" + r.nextInt(10);
        log("Userul sus = " + user);
        userulDePeThread.set(user);
        try {
            executor.submit(propagateUser(LaPiscina::m));
            executor.submit(propagateUser(LaPiscina::m));
        } finally {
            userulDePeThread.remove();
        }
        executor.shutdown();
    }

    private static Runnable propagateUser(Runnable treabaDeFapt) {
        // aici sunt pe threadul original
        String userulOriginal = userulDePeThread.get();
        return () -> {
            // aici sunt pe worker thread
//            Thread.currentThread().setName(userulOriginal); // optional
            userulDePeThread.set(userulOriginal);
            try {
                treabaDeFapt.run();
            } finally {
                userulDePeThread.remove();
            }
        };
    }

    private static void f() {
        g();
    }
private static final Logger log = LoggerFactory.getLogger(LaPiscina.class);
    private static void g() {
        // in repo, in beci: aici vrei userul curent sa-l pun in LAST_MODIFIED_BY
        log.debug("LAST_MODIFIED_BY = " + userulDePeThread.get());
    }

    public static void m() {
        log("Treaba de facut");

        sleep2(1000);
        f();

        log("Gata treaba");

    }
}
