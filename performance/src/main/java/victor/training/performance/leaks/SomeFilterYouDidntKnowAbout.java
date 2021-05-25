package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@Component
public class SomeFilterYouDidntKnowAbout implements Filter {

   private ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      BigObject20MB bigObject = new BigObject20MB();
//      threadLocal.set(bigObject);
      chain.doFilter(request, response);
   }
}
