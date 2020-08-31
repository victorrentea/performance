package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedList;

@RestController
@RequiredArgsConstructor
public class BatchingOnTheFly {
   private final FragileServiceClientAggregator aggregator;
   static AtomicInteger id = new AtomicInteger();
   @GetMapping
   public Beer getBeer() throws ExecutionException, InterruptedException {
      int uniqueBeerId = id.incrementAndGet();

      Beer beer = aggregator.getBeer(uniqueBeerId).get();

      return beer;
   }
}

@Service
@RequiredArgsConstructor
class FragileServiceClientAggregator {
   private final FragileServiceClient client;

   private List<Integer> idBuffer = new ArrayList<>();

   // []
   public Future<Beer> getBeer(int uniqueBeerId) {
      List<Integer> thisChunk;
      synchronized (this) {
         idBuffer.add(uniqueBeerId); // din cate threaduri ruleaza linia asta ?
         if (idBuffer.size() >= 2) {
            thisChunk = new ArrayList<>(idBuffer);
            idBuffer.clear();
         } else {
            return new CompletableFuture<>(); // un Future
         }
      }

      List<Beer> beers = client.getBeer(thisChunk);
      return CompletableFuture.completedFuture(beers.get(beers.size() - 1));
   }
}




// REST
@Service
@Slf4j
class FragileServiceClient {
   List<Beer> getBeer(List<Integer> orderIds) {
      log.info("Creez beri pt " + orderIds);
      ConcurrencyUtil.sleepq(1000);
      return orderIds.stream().map(Beer::new).collect(Collectors.toList());
   }
}

@Value
class Beer {
   int orderId;
}