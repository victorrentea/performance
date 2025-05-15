package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.list;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {
   private List<Access> lastTenAccesses = new ArrayList<>();

   record Access(
       String ip,
       Map<String, String> headers,
       LocalDateTime timestamp) {
   }
   @GetMapping
   public synchronized String endpoint(HttpServletRequest request) {
      var access = createaAccess(request);

      lastTenAccesses.add(access);
      if (lastTenAccesses.size() > 10) {
         lastTenAccesses = lastTenAccesses.subList(1, lastTenAccesses.size());
      }
      return "The current window size is " + lastTenAccesses.size();
   }

   private Access createaAccess(HttpServletRequest request) {
      var headers = list(request.getHeaderNames()).stream()
          .collect(toMap(name -> name, request::getHeader));
     return new Access(
         request.getRemoteAddr() + ":" + request.getRemotePort(),
         headers,
         LocalDateTime.now());
   }

   @GetMapping("many")
   public String mass(HttpServletRequest request) {
//      RestTemplate rest = new RestTemplate();
      for (int i = 0; i < 1_000; i++) {
//          rest.getForObject("http://localhost:8080/leak3", String.class);
         endpoint(request); // close enough for our experiment
      }
      return "The current window size is " + lastTenAccesses.size();
   }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

