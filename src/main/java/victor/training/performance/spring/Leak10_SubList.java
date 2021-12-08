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

   private List<BigObject20MB> lastRequestTimes = new ArrayList<>();

   @GetMapping
   public synchronized int test() {
      lastRequestTimes.add(new BigObject20MB());
      if (lastRequestTimes.size() > 5) {
         lastRequestTimes = lastRequestTimes.subList(1, lastRequestTimes.size());
      }
      return lastRequestTimes.size();
   }

}

