package victor.training.jfr;

public class TestJava11 {
   public static void main(String[] args) {
      System.out.println("Start");
      var j = 1;
      m(j);
   }

   private static void m(int j) {
      MyEvent event = new MyEvent();
      event.begin();
      long t0 = System.currentTimeMillis();
      int s = 1;
      while (true) {
         s += Math.sqrt(j * s);
         j++;
         if (System.currentTimeMillis() - t0 > 1000) {
            break;
         }
      }
      if (event.isEnabled()) {
         event.setOrderId(13L);
         event.commit();
      }
      System.out.println("End");
   }
}
