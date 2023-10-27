package victor.training.performance.spring;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

import javax.servlet.*;
import java.io.IOException;

class ReqMetaHolder {
   static final ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();
}
//@Component
class MyFilter implements Filter {

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      BigObject20MB bigObject = new BigObject20MB().setSomeString("john.doe"); // retrived from a network call
      ReqMetaHolder.threadLocalMetadata.set(bigObject);  // 🛑 remember to .remove() any ThreadLocal you have
      try {
         chain.doFilter(request, response);
      } finally {
//         ReqMetaHolder.threadLocalMetadata.remove();

         //
      }
   }
}
@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {

   @GetMapping
   public String test() {
//doWith(dataToStore, () -> {
         businessMethod1();
//});
      // DO NOT MANUALLY .set() a threadLocal in random code EVER.
      // almost always there are framework-provided ThreadLocals that you should use instead
      // SecurityContextHolder, @Scope("request" or "session")

      // ALSO, limitations:

         return "Magic can do harm.";
   }

   private void businessMethod1() { // no username in the signature
      businessMethod2();
   }

   private void businessMethod2() {
      // if you enter this code from another thread than HTTP request: @Scheduled, @RabbitListener
      // the thread local thing will be null
      BigObject20MB bigObject = ReqMetaHolder.threadLocalMetadata.get();
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
