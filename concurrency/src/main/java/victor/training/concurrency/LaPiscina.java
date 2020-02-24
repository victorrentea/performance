package victor.training.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class LaPiscina {

    static ThreadLocal<String> userulDePeThread = ThreadLocal.withInitial(() -> null);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);


        executor.submit(LaPiscina::m);
        executor.submit(LaPiscina::m);
        executor.shutdown();
    }

    private static void f() {
        g();
    }

    private static void g() {
        // in repo, in beci: aici vrei userul curent sa-l pun in LAST_MODIFIED_BY
        log("LAST_MODIFIED_BY = " + userulDePeThread.get());
    }

    public static void m() {
        log("Treaba de facut");
        String user = "gigi " + Math.random();
        log("Userul sus = " + user);
        userulDePeThread.set(user);
        try {
            sleep2(1000);
            f();

            log("Gata treaba");
        } finally {
            userulDePeThread.remove();
        }
    }
}
