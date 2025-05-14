package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak17")
public class Leak17_HotEndpointStarvingTomcatThreads {

   @GetMapping // call 200 times to saturate Tomcat's thread pool
   public void hotEndpoint() {
     tensorFlow();
   }

   private void tensorFlow() {
      sleepMillis(60_000); // pretend
   }

   @GetMapping("/liveness")
   public String liveness() {
     return "k8s, please don't kill me!";
   }
}
