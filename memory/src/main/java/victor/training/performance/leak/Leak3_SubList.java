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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {
   private List<Access> lastTenTimestamps = new ArrayList<>();

   record Access(String ip, LocalDateTime timestamp) {
   }
   @GetMapping
   public synchronized String endpoint(HttpServletRequest request) {
      lastTenTimestamps.add(new Access(request.getRemoteAddr()+":"+request.getRemotePort(), LocalDateTime.now()));
      if (lastTenTimestamps.size() > 10) {
         lastTenTimestamps = lastTenTimestamps.subList(1, lastTenTimestamps.size());
         // TODO ai said to use LinkedList .add / remove(0)
      }
      return "The current window size is " + lastTenTimestamps.size();
   }

   @GetMapping("many")
   public String mass(HttpServletRequest request) {
//      RestTemplate rest = new RestTemplate();
      for (int i = 0; i < 10_000; i++) {
//          rest.getForObject("http://localhost:8080/leak3", String.class);
         endpoint(request); // close enough for our experiment
      }
      return "The current window size is " + lastTenTimestamps.size() + ": " + lastTenTimestamps;
   }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

