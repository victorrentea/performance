package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak16")
public class Leak16_ThreadLocalCache {
   @GetMapping
   public String endpoint() {
      return Library.innocentMethod();
   }
}

// -- can't change code below this line: part of a library --
class Library {
   private static final ThreadLocal<ExpensiveResource> cache
       = ThreadLocal.withInitial(ExpensiveResource::new);
   // evaluates on first .get()

   public static String innocentMethod() {
      return "Meaning of life: " + cache.get().getMeaningOfLife();
      // deliberately no ThreadLocal#clear() as current thread is expected to be pooled and return back later
   }
   private static class ExpensiveResource {
      private final int meaningOfLife;
      private final BigObject20MB bigObject = new BigObject20MB();
      // cached on thread: a parsed schema (XSD, OpenApi/Swagger), an engine (Nashorn, Groovy)..

      public ExpensiveResource() {
         System.out.println("Searching for meaning of life...");
         sleepMillis(100); // takes time to compute
         meaningOfLife = 42;
      }

      public String getMeaningOfLife() {
         return "" + meaningOfLife;
      }
   }
}
