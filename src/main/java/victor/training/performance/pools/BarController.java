package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class BarController {
   @Autowired
   private BarService service;

   @GetMapping
   public CompletableFuture<DillyDilly> getDrinks() throws Exception {
      long t0 = System.currentTimeMillis();
      try {
         return service.orderDrinks();
      } finally {
         long t1 = System.currentTimeMillis();
         System.out.println("Delta : " + (t1 - t0));
      }
   }
}
