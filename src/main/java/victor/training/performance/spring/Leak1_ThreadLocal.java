package victor.training.performance.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS) // dubios in contetx de OpenID Connect.
class ReqScoped {
   BigObject20MB bigObject20MB;
}
@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();
   // e mai safe sa folosesti @Scope(request) caci il curata automat springul la sfarsitul requestului http;.

   @GetMapping
   public String test() {

//      SecurityContextHolder.getContext().getAuthentication().getName();
      BigObject20MB bigObject = new BigObject20MB();
      bigObject.someString = "john.doe"; // username

      threadLocalMetadata.set(bigObject);
      // REGULA intotdeauna dupa ce pui ceva pe un thread local, urmeaza sa faci

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
 * If you insist to use ThreadLocal, after the first .set(), always do try { } finally { .remove(); }
 */
