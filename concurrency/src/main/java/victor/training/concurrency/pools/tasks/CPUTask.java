package victor.training.concurrency.pools.tasks;

import victor.training.concurrency.ConcurrencyUtil;

import java.math.BigDecimal;

public class CPUTask implements Runnable {
    private final long targetN;

    public CPUTask(long targetN) {
        this.targetN = targetN;
    }

    @Override
    public void run() {
        cpu(targetN);
    }
    private static void cpu(long n) {
        for (int i = 0; i<n; i++) {
            new BigDecimal(i).pow(i);
        }
    }

    public static CPUTask selfCalibrate(int targetMillis) {
        System.out.print("Warming up... ");
        for (int i = 100; i<1000;i++) {
            cpu(i);
        }
        System.out.println("Done");
        System.out.println("Looking for n to take " +targetMillis +" ms of CPU on 1 thread");
        System.gc();
        for (long n = 100; ; n*=1.1) {
            long nn = n;
            int dt = ConcurrencyUtil.measureCall(() -> cpu(nn));
            if (dt > targetMillis) {
                System.out.println("n="+n);
                return new CPUTask(n);
            }
        }
    }

    public static void main(String[] args) {
        CPUTask cpu = CPUTask.selfCalibrate(100);
        System.out.println(ConcurrencyUtil.measureCall(() -> cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() -> cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() -> cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
        System.out.println(ConcurrencyUtil.measureCall(() ->    cpu.run()));
    }
}
