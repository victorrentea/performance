package victor.training.performance.leak.obj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
public class AFilterYouDidntKnowAbout implements Filter {

   private static final ThreadLocal<Big20MB> someFrameworkThreadLocal = new ThreadLocal<>();

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      if (httpRequest.getRequestURI().contains("leak8")) {
         log.debug("doFilter");
         Big20MB bigObject = new Big20MB();
         someFrameworkThreadLocal.set(bigObject); // mistake: no .remove()
         chain.doFilter(request, response);
      } else {
         chain.doFilter(request, response);
      }
   }
}
