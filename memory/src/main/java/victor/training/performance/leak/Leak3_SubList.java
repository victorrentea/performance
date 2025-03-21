package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {
  private List<Access> lastTenTimestamps = new ArrayList<>();

  record Access(
      String ip,
      Map<String, String> headers,
      LocalDateTime timestamp) {
  }

  @GetMapping
  public synchronized String endpoint(HttpServletRequest request) {
    Access access = new Access(
        request.getRemoteAddr() + ":" + request.getRemotePort(),
        extractHeaders(request),
        LocalDateTime.now());

    lastTenTimestamps.add(access);
    if (lastTenTimestamps.size() > 10) {
      lastTenTimestamps = lastTenTimestamps.subList(1, lastTenTimestamps.size());
    }
    return "The current window size is " + lastTenTimestamps.size();
  }

  @GetMapping("many")
  public String mass(HttpServletRequest request) {
//      RestTemplate rest = new RestTemplate();
    for (int i = 0; i < 1_000; i++) {
//          rest.getForObject("http://localhost:8080/leak3", String.class);
      endpoint(request); // close enough for our experiment
    }
    return "The current window size is " + lastTenTimestamps.size() + ": " + lastTenTimestamps;
  }

  public static Map<String, String> extractHeaders(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      headers.put(name, request.getHeader(name));
    }
    return headers;
  }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

