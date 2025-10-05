package victor.training.performance.leak;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class IdempotencyFilter extends HttpFilter {
  private final Set<Call> previousCalls = Collections.synchronizedSet(new HashSet<>());

  record Call(String idempotencyKey) {}

  // FIXME: clear every second calls older than 2 seconds (dedup window)

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request.getRequestURI().endsWith("leak11")) {
      String idempotencyKey = request.getHeader("Idempotency-Key");
      if (idempotencyKey == null) {
        reject(response, "Missing request header 'Idempotency-Key'");
        return;
      }
      boolean newIK = previousCalls.add(new Call(idempotencyKey));
      if (!newIK) {
        reject(response, "Duplicate call rejected!");
        return;
      }
    }

    chain.doFilter(request, response);
  }

  private void reject(HttpServletResponse response, String message) throws IOException {
    response.setStatus(400);
    response.setContentType("text/plain");
    response.getWriter().write(message);
  }
}

/**
 * ⭐️ KEY POINTS
 * ☣️ Most leaks occur in libraries or unknown code
 * ☣️ Instance fields of singleton are permanent ≈ 'static'
 * ☣️ Set a max size to any permanent unbounded collection: List, Set, Map<♾️keys
 * 👍 For a cache: use a library with max-count/key-ttl
 */

