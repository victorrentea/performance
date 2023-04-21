package victor.training.performance.spring;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@RestController
@RequestMapping("leak14")
@Slf4j
public class Leak14_ShutdownHook {

   @GetMapping
   public String add() {
      BigObject20MB big = new BigObject20MB();
      Runtime.getRuntime().addShutdownHook(new Thread(()->
              System.out.println("Clean some files: " + big)));
      return "Added";
   }

}
