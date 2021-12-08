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

   private List<BigObject20MB> lastest10Objects = new ArrayList<>();

   @GetMapping
   public synchronized int test() {
      lastest10Objects.add(new BigObject20MB());
      if (lastest10Objects.size() > 10) {
         lastest10Objects = lastest10Objects.subList(1, lastest10Objects.size());
      }
      return lastest10Objects.size();
   }

}

