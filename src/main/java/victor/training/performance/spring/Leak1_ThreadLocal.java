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
   public String test() { // ruleaza pe ce thread ? pe un thread dat de tomcat, din cele 200 ale lui maxim (10 in mod normal = fara load).
      // 10 threaduri x 20 = 200 MB
      // 200 thrdeaduri (sub stress) x 20MB = 4 GB
      // cand pui date in thread local dintr-un thread ce vine dintr-un thread pool >>> acel thread nu MOARE ci sta idle, si ulterior e refolosit.
      BigObject20MB bigObject = new BigObject20MB();
      bigObject.someString = "john.doe"; // username
      threadLocalMetadata.set(bigObject);
      // regula1: nu te juca cu ThreadLocaluri
      // regula2: daca te joci, imediat ce faci .set() urmeaza un try-finally{.remove();}

      try {
         businessMethod1();
      } finally {
         threadLocalMetadata.remove();
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
   }
}

/**
 * KEY POINTS:
 * !! Avoid creating ThreadLocal variables yourself - use the safe ones managed by framework:
 * - SecurityContextHolder, @Scope("request" or "session")
 * - @Transactional, Persistence Context, JDBC Connection
 * - Logback MDC
 * If you insist on using ThreadLocal, RULE=
 *    after the first .set(), always do try { .. } finally { .remove(); }
 */
