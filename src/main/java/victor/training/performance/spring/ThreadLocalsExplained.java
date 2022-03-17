package victor.training.performance.spring;

import lombok.RequiredArgsConstructor;

import static victor.training.performance.util.PerformanceUtil.sleepq;

public class ThreadLocalsExplained {
   public static void main(String[] args) {
      UnController controller = new UnController(new UnService(new UnRepo()));
      new Thread(() -> {controller.laIntrare("ion");}).start();
      new Thread(() -> {controller.laIntrare("maria");}).start();
   }
}

class UsernameHolder {

   private static String currentUsername;

   public static String getCurrentUsername() {
      return currentUsername;
   }

   public static void setCurrentUsername(String currentUsername) {
      UsernameHolder.currentUsername = currentUsername;
   }
}

@RequiredArgsConstructor
class UnController {
   private final UnService unService;

   public void laIntrare(String username) { // sau in vreun filtru magic de security
      UsernameHolder.setCurrentUsername(username);
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

class UnRepo {
   public void update() {
      System.out.println("UPDATE ... SET LAST_MODIFIED_BY=?   (de catre " + UsernameHolder.getCurrentUsername());
   }
}