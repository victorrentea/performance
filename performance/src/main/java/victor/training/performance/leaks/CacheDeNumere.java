package victor.training.performance.leaks;

public class CacheDeNumere {

   public static void main(String[] args) {


      Long l1 = 129L;
      Long l2 = 129L;

      m(l1,l2);
   }

   private static void m(Long l1, Long l2) {
      System.out.println(l1 == l2);
   }
}
