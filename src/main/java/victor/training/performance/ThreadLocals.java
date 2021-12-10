package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadLocals {
   public static void main(String[] args) {

      ExecutorService pool = Executors.newCachedThreadPool();
      for (int i = 0; i <10; i++) {
         int j = i;
         pool.submit(() -> {
            Layer1 target = new Layer1(new Layer2(new Layer3()));
            String u = "u" + j;
            log.debug("I am user " + u);
            target.method(u);
         });
      }
   }
}

class UserNameHolder {
   public static ThreadLocal<String> currentUsername = new ThreadLocal<>(); // look for thread scoped beans in Spring .

   // Spring by default propagates SpringSecurityContext, @Transaction, HttpSession, CorrelationId, tenantId
}

@RequiredArgsConstructor
class Layer1 {// controller
   private final Layer2 layer2;

   public void method(String u) {
      UserNameHolder.currentUsername.set(u);
      layer2.method();
   }
}
@RequiredArgsConstructor
class Layer2 {
   private final Layer3 layer3;

   public void method() {
      PerformanceUtil.sleepSomeTime(10,20);
      layer3.method();
   }
}

@Slf4j
class Layer3 {
   public void method() { // repository
      log.debug("UPDATE ... SET MODIFIED_BY=? " + UserNameHolder.currentUsername.get());
   }
}