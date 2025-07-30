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

      threadLocal.set(bigObject);

      method1();

      // 🛑 finally { ThreadLocal#remove()

      return "Magic can do harm.";
   }

   private void method1() { // no username in the signature
      method2();
   }

   private void method2() {
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
 * If you insist on using ThreadLocal, after the first .set(), always do try { } finally { .remove(); }
 */


