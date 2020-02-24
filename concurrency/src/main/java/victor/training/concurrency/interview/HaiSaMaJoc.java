package victor.training.concurrency.interview;

import lombok.extern.slf4j.Slf4j;
import victor.training.concurrency.ConcurrencyUtil;

@Slf4j
public class HaiSaMaJoc {
    public static void main(String[] args) {

        X x = new X();
        log.info("Inainte");

        log.debug("Aici vine {}", x);

        if (log.isDebugEnabled()) {
            log.debug("Aici vine {}", x.asDebugInfo());
        }

        log.info("Dupa");
    }

}
class X{
    public String asDebugInfo() {
        ConcurrencyUtil.sleep2(1000);
        return ":p";
    }
}
