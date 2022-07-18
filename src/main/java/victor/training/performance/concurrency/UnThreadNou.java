//package victor.training.performance.concurrency;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.FileReader;
//import java.io.Reader;
//import java.util.concurrent.Future;
//
//public class UnThreadNou {
//
//    public static void main(String[] args) {
//
//        Thread thread = new Thread(new MyWork());
////        thread.setDaemon();
//        thread.start();
//
//        thread.interrupt();
//
//        ThreadPoolTaskExecutor pool;
//
//        Future<?> future = pool.submit(() -> {
//        });
//
//
//        future.cancel(true); // face Thread.interrupt
//
//
//        f(1);
//    }
//
//    private static void f(int i) {
//
//
//    }
//}
//
//@Slf4j
//class MyWork implements Runnable {
//
//    @Override
//    public void run() {
//        log.info("Intr-un alt thread");
//        HttpServletRequest request;
//        Reader fileReader = request.getReader();
//        int read = fileReader.read(); // blocheaza threadul desi NU arunca InterruptedException.
//        // daca cineva face .interrupt la acest thread, read se termina si trebuie sa te uiti la
//        boolean nuCumvaMiachematcinevainterruptCatEramInRead = Thread.currentThread().isInterrupted();
//        if (nuCumvaMiachematcinevainterruptCatEramInRead) {
//            throw new IllegalArgumentException("Valeu opresc exceutia");
//        }
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new IllegalArgumentException(e);
//        }
//        // alte chestii dure iau minute
//    }
//}