package victor.training.performance.spring;

public class IntegerCache {
   public static void main(String[] args) {
      method(10000l, 10000l);
   }

   public static void method(Long id1, Long id2) {
      if (id1 == id2) {
         System.out.println("URAA!");
      } else {
         System.out.println("BUG: nu stii java. Pune si in Eclipseul tau SonarLint, te rog io!");
      }
   }
}

