package victor.training.concurrency;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CumSaLogez {
    public static void main(String[] args) {

        int x = 1;
        if (log.isDebugEnabled()) {
            log.debug("X=" + x);
        }
        // identic cu:
        log.debug("X={}", x);

        if (log.isDebugEnabled()) {
            log.debug("X = {}", calcul(x));
        }
    }

    private static String calcul(int x) {
        ConcurrencyUtil.sleep2(1000);
        return null;
    }
}
