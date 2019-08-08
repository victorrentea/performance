package victor.training.concurrency.db;

import victor.training.concurrency.ConcurrencyUtil;
import victor.training.concurrency.ThreadPool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class StapanDeSclavi {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> taskuri = IntStream.range(1, 22).boxed().collect(toList());

//        ExecutorService pool = Executors.newSingleThreadExecutor(); // Si era una la parinti
//        ExecutorService pool = Executors.newFixedThreadPool(2); // are o coada infinita. Poate fi ne realis ta permiti 10000 de taskuri in coada.
//        ExecutorService pool = Executors.newCachedThreadPool(); // periculos caci iti poate umple casa de threaduri. OS poate sa cada

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(20); // asta are capacitate finita
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,
                2,
                1, TimeUnit.SECONDS, // nu e necesar daca core = max pool size
                queue);

        log("Start");
        for (Integer id : taskuri) {
            pool.execute(() -> taskGreu(id));
//            queue.send(new TextMessage(id)); // similar
        }
        log("Am submis toate taskurile");
        pool.shutdown();
        log("Dau stingerea");
        pool.awaitTermination(1, TimeUnit.MINUTES);
        log("Chiar s-au culcat toti");
    }
    public static void taskGreu(int id) {
        log("Incep treaba " + id);
        sleep2(1000); // IO sau CPU intensive // chem baza de date.
        log("Termin treaba " + id);
    }
}
