package victor.training.performance.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@Scope(value = "request", proxyMode= ScopedProxyMode.TARGET_CLASS)
@Component
class MetaHolder {

}

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   private static final ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB().setSomeString("john.doe"); // retrived from a network call

      threadLocalMetadata.set(bigObject);  // 🛑 remember to .remove() any ThreadLocal you have

      try {
         businessMethod1();
      } finally { // pattern. if ever playing manually with thread locals. DON'T !!!!
         threadLocalMetadata.remove(); // detaches data from thread that's going to be reused later
      }
      // more examples: you leave thread locals on threads from:
      // - MQ listeners
      // - @Scheduled
      // - HTTP requests
      // ideas:  -> @Aspect/Interceptor to clean the TLocal after the metod completes
      // Alternative to Thread Local only for HTTP : @Scope("request", proxyMode=Class) @Component -> auto-cleaned the


      return "Magic can do harm.";
   }

   private void businessMethod1() { // no username in the signature
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocalMetadata.get();
      String currentUsernameOnThisThread = bigObject.someString;
      System.out.println("Business logic using " + currentUsernameOnThisThread);
      // TODO what if throw new RuntimeException(); ?
   }
}

/**
 * KEY POINTS:
 * !! Avoid creating ThreadLocal variables yourself - use the safe ones managed by framework:
 * - SecurityContextHolder, @Scope("request" or "session")
 * - @Transactional, Persistence Context, JDBC Connection
 * - Logback MDC
 * If you insist to use ThreadLocal, after the first .set(), always do try { } finally { .remove(); }
 */
