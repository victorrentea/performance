package victor.training.performance.swing;

import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

public class Play {
    int x; // heap
   static int xstatic; // heap

   public  synchronized static void main(String[] args) {
//      new ThreadPoolExecutor()
      ThreadFactory threadFactory = new ThreadFactory() {

         @Override
         public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
         }
      };

      int localVar = 0;

      Supplier<Integer> s = () -> localVar;

//      field = s;
//      sleep():
//
//      localVar ++;
//      s
//      new Thread(() -> {  s.get(); }).start();;

//      Connection connection;
//      PreparedStatement ps = connection.prepareStatement("INSER ");
//      ps.executeUpdate();
//
//
//      new Thread(() -> {
//         PreparedStatement ps = connection.prepareStatement("INSER ");
//
//      })

   }
}
