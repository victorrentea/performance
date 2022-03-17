package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static victor.training.performance.util.PerformanceUtil.sleepq;

public class ThreadLocalsExplained {
   public static void main(String[] args) {
      UnController controller = new UnController(new UnService(new UnRepo()));
      new Thread(() -> {controller.laIntrare("ion");}).start();
      new Thread(() -> {controller.laIntrare("maria");}).start();
   }
}

class UsernameHolder {
   private static ThreadLocal<String> currentUsername  = ThreadLocal.withInitial(() -> "anonymous");

   public static String getCurrentUsername() {
      return currentUsername.get();
   }

   public static void setCurrentUsername(String currentUsername) {
      UsernameHolder.currentUsername.set(currentUsername);
   }
}

@Slf4j
@RequiredArgsConstructor
class UnController {
   private final UnService unService;

   public void laIntrare(String username) { // sau in vreun filtru magic de security
      UsernameHolder.setCurrentUsername(username);
      log.info("Sunt userul: " + username);
      unService.met();
   }
}

@RequiredArgsConstructor
class UnService {
   private final UnRepo unRepo;

   public void met() {
      sleepq(10);
      unRepo.update();
   }
}

@Slf4j
class UnRepo {
   public void update() {
      log.info("UPDATE ... SET LAST_MODIFIED_BY=?   (de catre " + UsernameHolder.getCurrentUsername());
   }
}