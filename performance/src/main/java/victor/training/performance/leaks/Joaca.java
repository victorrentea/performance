package victor.training.performance.leaks;

public class Joaca {

   public static void main(String[] args) {
      Long i1 = 129L;
      Long i2 = 129L;

      method(i1, i2);
   }

   public static void method(Long id1, Long id2) {
       if (id1 == id2) {
          System.out.println("Eram in eclipse");
       }
   }
}
