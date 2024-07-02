package victor.training.performance.leak;

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
   private List<LocalDateTime> lastTenTimestamps = new ArrayList<>();

   @GetMapping
   public synchronized String endpoint() {
      lastTenTimestamps.add(LocalDateTime.now());
      if (lastTenTimestamps.size() > 10) {
         lastTenTimestamps = lastTenTimestamps.subList(1, lastTenTimestamps.size());
      }
      return "The current window size is " + lastTenTimestamps.size();
   }

   @GetMapping("mass")
   public String mass() {
      for (int i = 0; i < 10_000; i++) {
         endpoint();
      }
      return "Executed 10K calls. " + endpoint();
   }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

