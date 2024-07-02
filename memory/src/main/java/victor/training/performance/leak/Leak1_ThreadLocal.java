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
      try { // imediat dupa set() se face un try-finally cu remove() > REGULA!
         // daca-ti faci tu in curte un ThreadLocal.
         // Recomandarea e sa NU pui mana manual tu muritor pe ThreadLocal ci sa folosesti ceva de framework
         // eg: SeciurityContextHolder, MDC.put(   dar fara sa pui prea multe date acolo)
         // Remember: vin Virtual Threads si orice date pui pe thread x 100K threaduri (worst case)
         businessMethod1();
      } finally {
         threadLocal.remove();
      }


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
