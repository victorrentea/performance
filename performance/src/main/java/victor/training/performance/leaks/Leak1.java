package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.*;
import java.io.IOException;

import static victor.training.performance.leaks.SomeFilter.threadLocal;

@RestController
@RequestMapping("leak1")
public class Leak1 {
   @GetMapping
   public String test() {
      businessMethod1();
      return "Magic can do harm.";
   }

   private void businessMethod1() {
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocal.get();
      System.out.println("Business logic using " + bigObject);
      // TODO think of throw new RuntimeException();
   }
}


@Slf4j
@Component
class SomeFilter implements Filter {
   public static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      log.debug("In a servlet filter");
      BigObject20MB bigObject = new BigObject20MB();
      try {
         threadLocal.set(bigObject);
      } finally {
         threadLocal.remove();
      }
      chain.doFilter(request, response);
   }
}