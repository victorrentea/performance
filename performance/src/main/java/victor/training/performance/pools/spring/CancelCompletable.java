package victor.training.performance.pools.spring;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import victor.training.performance.pools.BarmanJavaSE;
import victor.training.performance.pools.Beer;
import victor.training.performance.pools.Vodka;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class CancelCompletable {
    public static void main(String[] args) throws InterruptedException {


//        FJP
        ExecutorService pool = Executors.newFixedThreadPool(10);

        List<Future<?>> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Future<?> futureVoid = pool.submit(new TaskCancel(i));
            list.add(futureVoid);
        }
        Thread.sleep(500);
        list.forEach(c -> c.cancel(true)); // true nu conteaza.
        // dar cancel pare ca merge: nu mai ruleaza celelate


        Thread.sleep(5000);


    }
}

@Data
@Slf4j
class TaskCancel implements Runnable{
    private final int i;

    @SneakyThrows
    @Override
    public void run() {

//        Writer writer;
//        writer.write("date"); //daca in timpul write-ului primesti un interrupt
//        // trebuie sa verifici
//        if (Thread.interrupted()) {
//            System.out.println("Sunt mort");
//            return;
//        }
        log.debug("START " + i);
        try {
            Thread.sleep(1000);
            Future<String > fs;
//            fs.get()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("DONE " + i);
    }
}
