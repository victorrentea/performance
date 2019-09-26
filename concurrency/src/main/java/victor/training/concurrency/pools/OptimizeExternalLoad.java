package victor.training.concurrency.pools;

import org.jooq.lambda.Unchecked;
import victor.training.concurrency.pools.tasks.LimitedExternalSystemTask;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.concurrency.ConcurrencyUtil.measuring;

public class OptimizeExternalLoad {

    public static void main(String[] args) {
        // cannot tune, global in App Server, shared for other requests
        ExecutorService httpPool = Executors.newFixedThreadPool(10);

        BizService service = new BizService();
        List<Future<Integer>> futures = IntStream.range(0, 20)
                .mapToObj(i -> httpPool.submit(measuring(service::execute)))
                .collect(toList());

        double avg = futures.stream()
                .map(Unchecked.function(Future::get))
                .mapToInt(Integer::intValue)
                .average()
                .getAsDouble();

        System.out.println(avg + " ms");
        if (avg > 500) {
            System.out.println("ERROR: Exceeded my NFR of 500 ms! :(");
        }
        httpPool.shutdown();
    }
}

class BizService {
    // @Autowired
    private LimitedExternalSystemTask limited = new LimitedExternalSystemTask();

    public void execute() {
        // do important logic

        // here, call the external system
        limited.run();
    }


}