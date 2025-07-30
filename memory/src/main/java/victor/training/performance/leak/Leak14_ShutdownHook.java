package victor.training.performance.leak;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

@RestController
@RequestMapping("leak14")
@Slf4j
public class Leak14_ShutdownHook {

   @GetMapping
   public String add() {
      OldLib.stuff();
      return "All good";
   }

}

class OldLib { // designed to be used in a desktop/console/job app
   public static void stuff() {
      BigObject20MB big = new BigObject20MB(); // cleanup internal stuff
      Runtime.getRuntime().addShutdownHook(new Thread(()->
          System.out.println("Clean some files: " + big)));
   }

}
