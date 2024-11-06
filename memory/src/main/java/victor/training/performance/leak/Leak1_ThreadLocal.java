package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   private static final ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   @GetMapping
   public String endpoint() {
      BigObject20MB bigObject = new BigObject20MB().setSomeString("john.doe"); // retrived from a network call

      threadLocal.set(bigObject);  // 🛑 ThreadLocal#remove()

//      sleep(100000); // would allow to manifest this from browser
      businessMethod1();

      return "Magic can do harm.";
   }

   private void businessMethod1() { // no username in the signature
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocal.get();
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
