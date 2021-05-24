package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class BarController {
   @Autowired
   private BarService service;
   @Autowired
   private MyRequestContext requestContext;

   @GetMapping
   public CompletableFuture<DillyDilly> getDrinks() throws ExecutionException, InterruptedException {
      try {
         return service.orderDrinks();
      } finally {
         log.info("Returning in the pool to play with my siblings, http worker threads");
      }
   }
}
