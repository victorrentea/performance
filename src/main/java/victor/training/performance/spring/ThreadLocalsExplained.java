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


@RequiredArgsConstructor
class UnController {
   private final UnService unService;

   public void laIntrare(String username) { // sau in vreun filtru magic de security
      unService.met(username);
   }
}

@RequiredArgsConstructor
class UnService {
   private final UnRepo unRepo;

   public void met(String username) {
      sleepq(10);
      unRepo.update(username);
   }
}

class UnRepo {
   public void update(String username) {
      System.out.println("UPDATE ... SET LAST_MODIFIED_BY=?   (de catre " + username);
   }
}