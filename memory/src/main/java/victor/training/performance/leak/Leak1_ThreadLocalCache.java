package victor.training.performance.leak;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocalCache {// TODO
   private static final ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   @GetMapping
   public String endpoint() {
      businessMethod1();
      return "Magic can do harm.";
   }

   private void businessMethod1() { // no username in the signature
      businessMethod2();
   }

   private static final ExecutorService executors =
       Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
   @SneakyThrows
   private void businessMethod2() {

      Integer result = CompletableFuture.supplyAsync(() -> OldLib.libraryMethod("data")).get();

   }


}
class OldLib {
   private static final ThreadLocal<ExpensiveResource> threadLocalResource =
       ThreadLocal.withInitial(ExpensiveResource::new);
   public static int libraryMethod(String param) {
      threadLocalResource.get().use();

      // I will not clear away the thread local intentially
      // because in Java thread are reused from a pool -> it's likly that this thread will come back to me later.
      // "let's leave data on it for next time"
      // if many threads use this lib => each of those threads is gonna get a new ExpensiveResource forver attached to them
      return 0;
   }
}
class ExpensiveResource { // objects underlying a parsed schema: XSD, OpenApi/Swagger, WSDL
   @SneakyThrows
   public ExpensiveResource() {
      System.out.println("ExpensiveResource created");
      Thread.sleep(100);
   }

   public void use() {

   }
}
