package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
         log.debug("Threadul de HTTP se intoarce in piscina langa cei 200 de frati ai lui");
      }
   }
   @GetMapping("servlet")
   public void getDrinksJavaServlet(HttpServletRequest request) throws ExecutionException, InterruptedException {
      AsyncContext asyncContext = request.startAsync();

      service.orderDrinks()
          .thenAccept(dilly -> {
             try {
                asyncContext.getResponse().getWriter().write(dilly + "");
                asyncContext.complete();
             } catch (IOException e) {
                throw new RuntimeException(e);
             }
          });
      log.debug("Threadul de HTTP se intoarce in piscina langa cei 200 de frati ai lui");
   }
}
