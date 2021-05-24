package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@Component
public class SomeFilterYouDidntKnowAbout implements Filter {

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      log.debug("In a servlet filter");
      BigObject20MB bigObject = new BigObject20MB();
      Leak1_ThreadLocal.threadLocal.set(bigObject);
      chain.doFilter(request, response);
   }
}
