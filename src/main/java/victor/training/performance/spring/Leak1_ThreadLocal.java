package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      bigObject.someString = "john.doe"; // username
      threadLocalMetadata.set(bigObject);
      try {
         businessMethod1();
      } finally {
         threadLocalMetadata.remove(); // regula: daca te joci cu ThreadLocals: dupa ce ai facut .set() vine un try {}finally {.remove()};
         // PENTRU CA UITAM sa facem asta, e CLAR recomandat sa folosesti
         // @Sccope("request") pt ca Springul face cleanup dupa tine
      }
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
      throw new RuntimeException("Oups");
   }
}

/**
 * Avoid creating ThreadLocal variables yourself - use the safe ones managed by framework:
 * - SecurityContextHolder, @Scope("request" or "session")
 * - @Transactional, Persistence Context, JDBC Connection
 * - Logback MDC
 * If you still do it, after the first .set(), always follow with a try { } finally { .remove(); }
 */
