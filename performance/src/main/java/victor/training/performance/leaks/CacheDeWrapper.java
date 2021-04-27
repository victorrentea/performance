package victor.training.performance.leaks;

public class CacheDeWrapper {

   public static void main(String[] args) {
      method(1L,1L);
      method(10L,10L);
      method(127L,127L);
      method(128L,128L); // de aici incolo se face NEW
      method(200L,200L);
      method(300L,300L);
      method(10023L,10023L);


      System.out.println("a1" == "a"+1); // explicatie : String Common Pool
   }

   public static void method(Long id1, Long id2) {
      if (id1.equals(id2)) {
         System.out.println("EGALE, tre sa updatez");
      } else {
         System.out.println("NU EGALE");
      }
   }
}
