package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak3")
public class Leak3_SubList {

//   private List<BigObject20MB> lastTenObjects = new ArrayList<>();
   private Deque<BigObject20MB> lastTenObjects = new LinkedList<>(); // queue

   @GetMapping
   public synchronized String endpoint() { // running window
      lastTenObjects.add(new BigObject20MB());
      if (lastTenObjects.size() > 10) {
          lastTenObjects.removeFirst(); // STERGE PE BUNE
//         lastTenObjects = lastTenObjects.subList(1, lastTenObjects.size());
      }
      return "The current window size is " + lastTenObjects.size();
   }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

