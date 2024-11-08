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
class Library {
   private static final ThreadLocal<ExpensiveResource> cache
       = ThreadLocal.withInitial(ExpensiveResource::new);
   public static String innocentMethod() {
      return cache.get().use();
   }
}
class ExpensiveResource {
   // reality = parsed schema (XSD, OpenApi/Swagger), an engine (Nashorn, Groovy)..
   private final int meaning;
   private final BigObject20MB bigObject = new BigObject20MB();

   public ExpensiveResource() {
      System.out.println("Finding meaning of life...");
      sleepMillis(100); // takes time
      meaning = 42;
   }

   public String use() {
      return "Answer is " + meaning;
   }
}
