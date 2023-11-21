package victor.training.performance.spring;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

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
         // de ce raman datele Thread Local in viata dupa ce requestul curent se termina?
         // ce se intampla cu threadu curent dupa ce se termina requestul ?
         // se intoarce in thread pool cu tot cu THreadLocal de 20MB dupa el.
         // ThreadPool+ThreadLocal!=❤️
         // deci mereu inainte sa iesi faci:
      } finally {
         threadLocalMetadata.remove(); // pattern tl.set>try{}finally{tl.remove}
      }
      // VICTOR ZICE:
      // EVITA SA LUCREZI TU CU THREAD LOCALURI. In schimb:
      // @Scope("request") spring
      // @Scope(thread) cauta pe net
      // MDC Slf4j %X{tenantId}
      MDC.put("tenantID", "abc"); // logging
      // Daca totusi trebuie ThreadLocal, ai grija pe toate threadurile care intr ain codul tau
      // sa faci set;try{}finally{remove}}
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
