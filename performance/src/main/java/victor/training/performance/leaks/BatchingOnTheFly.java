package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import java.util.*;
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
   @GetMapping("batch")
   public Beer getBeer() throws ExecutionException, InterruptedException {
      int uniqueBeerId = id.incrementAndGet();

      Beer beer = aggregator.getBeer(uniqueBeerId).get();

      return beer;
   }
}

@Slf4j
@Service
@RequiredArgsConstructor
class FragileServiceClientAggregator {
   private final FragileServiceClient client;

   private Map<Integer, CompletableFuture<Beer>> idBuffer = new HashMap<>();

   // []
   public Future<Beer> getBeer(int uniqueBeerId) {

      if (log.isDebugEnabled()) {
         log.debug("Stuff " + uniqueBeerId + " of " + idBuffer.size());
      }
//         log.debug(new StringBuilder().append("Stuff ").append(uniqueBeerId).append(" of ").append(idBuffer.size()).toString()); // rau ca face char* v = malloc
      log.debug("Stuff {} of {}", uniqueBeerId, idBuffer.size());

      if (log.isTraceEnabled()) {
         log.trace("Stuff {} of {}", uniqueBeerId, functieScumpaChemataAici(idBuffer));
      }


      Map<Integer, CompletableFuture<Beer>> thisChunk;
      synchronized (this) {

         CompletableFuture<Beer> futureBeer = new CompletableFuture<>();
         idBuffer.put(uniqueBeerId, futureBeer); // din cate threaduri ruleaza linia asta ?

         if (idBuffer.size() >= 2) {
            //aici s-a umplut bufferul si chem fragile service
            thisChunk = new HashMap<>(idBuffer);
            idBuffer.clear();
         } else {
            return futureBeer; // un Future
         }
      }



      List<Beer> beers = client.getBeer(thisChunk.keySet());

      for (Beer beer : beers) {
         CompletableFuture<Beer> futureBeer = thisChunk.get(beer.getOrderId());
         futureBeer.complete(beer);
      }

      return thisChunk.get(uniqueBeerId);
   }

   private String functieScumpaChemataAici(Map<Integer, CompletableFuture<Beer>> idBuffer) {
      ConcurrencyUtil.sleepq(1000);
      return "chestii";
   }
}




// REST
@Service
@Slf4j
class FragileServiceClient {
   List<Beer> getBeer(Collection<Integer> orderIds) {
      log.info("Creez beri pt " + orderIds);
      ConcurrencyUtil.sleepq(1000);
      return orderIds.stream().map(Beer::new).collect(Collectors.toList());
   }
}

@Value
class Beer {
   int orderId;
}