package victor.training.performance.spring;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(value = "request", proxyMode = TARGET_CLASS) // for HTTP onl;y flows
@Component
@Data
class MetaHolder {
   BigObject20MB bigObject;
}

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   private static final ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();

   private final MetaHolder metaHolder;

   public Leak1_ThreadLocal(MetaHolder metaHolder) {
      this.metaHolder = metaHolder;
   }

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB().setSomeString("john.doe"); // retrived from a network call

      metaHolder.setBigObject(bigObject);
//      threadLocalMetadata.set(bigObject);  // 🛑 remember to .remove() any ThreadLocal you have

//      try {
         businessMethod1();
//      } finally { // pattern. if ever playing manually with thread locals. DON'T !!!!
//         threadLocalMetadata.remove(); // detaches data from thread that's going to be reused later
//      }

      // .more examples: you leave thread locals on threads from:
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
//      BigObject20MB bigObject = threadLocalMetadata.get();

      // this would fail if called from a MQ listener, since there is no HTTP REQUEST FOR THAT!
      BigObject20MB bigObject = metaHolder.getBigObject();

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
