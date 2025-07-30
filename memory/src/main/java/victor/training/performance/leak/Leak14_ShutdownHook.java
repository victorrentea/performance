package victor.training.performance.leak;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

import java.util.ArrayList;
import java.util.List;

import static victor.training.performance.leak.ShutdownHookInspector.cleanHooks;

@RestController
@RequestMapping("leak14")
@Slf4j
public class Leak14_ShutdownHook {

   @GetMapping
   public String add() throws Exception {
      OldLib.stuff();
      List<Thread> hooks = new ArrayList<>();

      cleanHooks();
      // i'm in a server. than [never] shuts down
      return "All good";
   }
}

// --- can't change the lib ---
class OldLib { // designed to be used in a desktop/console/job app
   public static void stuff() {
      BigObject20MB big = new BigObject20MB();
      Runtime.getRuntime().addShutdownHook(new Thread(()->
          System.out.println("Cleanup at JVM shutdown: " + big)));
   }

}
