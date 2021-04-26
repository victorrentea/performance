package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.ConcurrencyUtil;

@Slf4j
public class ThreadLocalExperiment {
   public static void main(String[] args) {
      new Thread(() -> controller("a")).start();
      new Thread(() -> controller("b")).start();


   }
   static ThreadLocal<String> currentUser = new ThreadLocal<>();
   // current username @Resource Principal / SecurityContextHolder.getContext().getPrincipal().getName()

   public static void controller(String usernameDinHttp) {
      log.info("Set : " + usernameDinHttp);
      currentUser.set(usernameDinHttp);
      ConcurrencyUtil.sleepq(100);
      repoMethod();
   }

   private static void repoMethod() {
      log.info("GET : " + currentUser.get());
   }
}
