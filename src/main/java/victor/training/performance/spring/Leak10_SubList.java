package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak10")
public class Leak10_SubList {

   private List<BigObject20MB> lastTenObjects = new ArrayList<>();

   @GetMapping
   public synchronized int test() {
      lastTenObjects.add(new BigObject20MB());
      if (lastTenObjects.size() > 10) {
         lastTenObjects = lastTenObjects.subList(1, lastTenObjects.size());
      }
      return lastTenObjects.size();
   }
}

/**
 * KEY POINTS
 * - .subList() returns a projection over the original array => the original list is kept referenced
 * - RTFM: https://www.google.com/search?q=RTFM
 * - A LinkedList is better here
 */

