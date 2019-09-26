package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class JudoLocks {
    public final Object arbiter = new Object();
    public static void main(String[] args) {
        Yin yin = new Yin();
        Yang yang = new Yang();
        yin.setYang(yang);
        yang.setYin(yin);

        new Thread(yin::lupta, "Yin").start();
        new Thread(yang::lupta, "Yang").start();
    }

}

class Yin {
    private Yang yang;
    public void setYang(Yang yang) {
        this.yang = yang;
    }
    public void lupta() {
        log("Mai ai nevoie de mana asta?");
        yang.sucesteMana();
    }
    public void strangeDeGat() {
        log("Cu sufletul la gura!");
        sleep2(1000);
        log("Renunt!");
    }
}
class Yang {
    private Yin yin;
    public void setYin(Yin yin) {
        this.yin = yin;
    }
    public void lupta() {
        log("Ce gat delicat ai!");
        yin.strangeDeGat();
    }
    public void sucesteMana() {
        log("Ai-ai-ai!! U-uu-uu!!");
        sleep2(1000);
        log("Renunt!");
    }
}