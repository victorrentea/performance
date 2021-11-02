package victor.training.performance.spring;

import static victor.training.performance.util.PerformanceUtil.sleepq;

public class ThreadLocals {
}

class SecurityContextHolderVictor {

   private static String nuMerge;
   private static ThreadLocal<String> currentUsername = new ThreadLocal<>();

   public static void setCurrentUsername(String currentUsername) {
      SecurityContextHolderVictor.currentUsername.set(currentUsername);
//      nuMerge = currentUsername;
   }
   public static String getCurrentUsername() {
      return currentUsername.get();
//      return nuMerge;
   }
}


class FiltreDeSecu {
   private Controller controller = new Controller();
   public void method(String gigi) {
      String username = gigi;
      SecurityContextHolderVictor.setCurrentUsername(username);
      sleepq(1);
      controller.method();
   }

   public static void main(String[] args) {
      new Thread(()->new FiltreDeSecu().method("gigi")).start();
      new Thread(()->new FiltreDeSecu().method("mimi")).start();
   }
}
class Controller {
   Service service = new Service();

   public void method() {
      service.method();
   }

}
class Service {
   private Repository repo=new Repository();
   public void method() {
      repo.method();
   }
}
class Repository {
   public void method() {
      String currentUsername = SecurityContextHolderVictor.getCurrentUsername();//SecurityContextHolder.getContext().getAuthentication().getName();
      System.out.println("INSERT INTO BLA(...,CREATED_BY) VALUES (..., " + currentUsername + ")");
   }
}
