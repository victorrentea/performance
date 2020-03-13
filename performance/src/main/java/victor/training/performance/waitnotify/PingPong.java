package victor.training.performance.waitnotify;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleep2;

public class PingPong {
    public static final int PING = 1;
    public static final int PONG = 2;
    public static int lastPlayer = PONG;

    public static void main(String[] args) {
        new Ping("Ping1").start();
        new Ping("Ping2").start();
        new Pong("Pong1").start();
        new Pong("Pong2").start();
        log("Le-am pornit");


//        sleep2(100000);
    }
}

class Ping extends Thread {
    public Ping(String name) {
        super(name);
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            synchronized (PingPong.class) {
                try {
                    while (PingPong.lastPlayer == PingPong.PING) {
                        PingPong.class.wait();
                    }
                    PingPong.lastPlayer = PingPong.PING;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
            sleep2(100);
            log("Ping");
            sleep2(100);
//            synchronized (PingPong.class) {
                PingPong.class.notifyAll();
            }
        }
        synchronized (Pong.class) {
            Pong.class.notifyAll();
        }
    }

}

class Pong extends Thread {
    public Pong(String name) {
        super(name);
    }

    int counter = 0;

    public void run() {
        for (int i = 0; i < 100; i++) {
            synchronized (PingPong.class) {
                try {
                    while (PingPong.lastPlayer == PingPong.PONG) {
                        PingPong.class.wait();
                    }
                    PingPong.lastPlayer = PingPong.PONG;
                } catch (InterruptedException e) {
                    // shaorma
                }
//            }
            sleep2(100);
            log("Pong " + ++counter);
            sleep2(100);
//            synchronized (PingPong.class) {
                PingPong.class.notifyAll();
            }
        }
        synchronized (Ping.class) {
            Ping.class.notifyAll();
        }
    }

}


