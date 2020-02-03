package victor.training.concurrency.leaks;

import lombok.extern.slf4j.Slf4j;
import victor.training.concurrency.ConcurrencyUtil;

@Slf4j
public class ThreadLocalUnleashed {


    public static void main(String[] args) {
        new Thread(ThreadLocalUnleashed::m).start();
        new Thread(ThreadLocalUnleashed::n).start();
    }
    static ThreadLocal<String> a = ThreadLocal.withInitial(() -> null);

    static public void m() {
        log.debug("Setez variabila pe X");
        a.set("X");
        //
        oAltaMetodaZeceLayereMaiJos();
    }

    private static void oAltaMetodaZeceLayereMaiJos() {
        ConcurrencyUtil.sleep2(1000);
        log.debug("Am  LAST_MODIFIED_BY=? " + a.get());
    }

    static public void n() {
        log.debug("Setez variabila pe Y");
        a.set("Y");
        //
        ConcurrencyUtil.sleep2(1000);
        log.debug("Am  gasit " + a.get());
    }
}
