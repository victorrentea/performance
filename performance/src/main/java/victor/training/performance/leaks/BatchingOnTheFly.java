package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

   private List<Integer> idBuffer;

   // []
   public Future<Beer> getBeer(int uniqueBeerId) {
      idBuffer.add(uniqueBeerId);
      if (idBuffer.size() >= 2) {
         List<Beer> beers = client.getBeer(idBuffer);
         idBuffer.clear();

//         ArrayBlockingQueue
//         Futures
         return CompletableFuture.completedFuture(beers.get(beers.size() - 1));
      }
      return null; // cum intorc aici un Future<> care sa-l pot termina manual la linia 47?
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