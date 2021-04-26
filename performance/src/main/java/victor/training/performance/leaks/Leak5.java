package victor.training.performance.leaks;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@RestController
@RequestMapping("leak5")
public class Leak5 {

   // CATE DOI, CATE DOI ... : https://youtu.be/V798MhKfdZ8

   @GetMapping
   public String root() throws Exception {
      return "call <a href='./leak5/one'>/one</a> and <a href='./leak5/two'>/two</a> withing 3 secs..";
   }

   @GetMapping("/one")
   public String one() throws Exception {
      KillOne.entryPoint();
      return "--> You didn't call /two within the last 3 secs, didn't you?..";
   }

   @GetMapping("/two")
   public String two() throws Exception {
      KillTwo.entryPoint();
      return "--> You didn't call /one within the last 3 secs, didn't you?..";
   }
}


@Service
class KillOne {
//   public synchronized void f() {
//      synchronized (this) {
//      	//chestii
//      }
//   }
   public static synchronized void entryPoint() { // /one cheama asta din Th1
      synchronized (KillOne.class) {
			log("start One.a1()");
         sleepq(3_000);
         KillTwo.internalMethod();
         log("start One.a1()");
      }
   }
   public static void internalMethod() {
      synchronized (KillOne.class) {
			log("start One.b1()");
			sleepq(3_000);
			log("end One.b1()");
		}
   }
}
class KillTwo {
   public static void entryPoint() { // /two cheama asta din Th2
      synchronized (KillTwo.class) {
			log("start Two.a2()");
			sleepq(3_000);
			KillOne.internalMethod();
			log("start Two.a2()");
		}
   }
   public static void internalMethod() {
      synchronized (KillTwo.class) {
			log("start Two.b2()");
			sleepq(3_000);
			log("end Two.b2()");
		}
   }
}
