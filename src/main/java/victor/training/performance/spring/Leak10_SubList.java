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
   public synchronized int test() { // TODO arde-l de 20 de ori si ai vrea sa vezi doar 10 * 20 = 200 dar vei gasii 400 de MB
      lastTenObjects.add(new BigObject20MB());
      if (lastTenObjects.size() > 10) {
         lastTenObjects = lastTenObjects.subList(1, lastTenObjects.size());
      }
      return lastTenObjects.size();
   }

}

