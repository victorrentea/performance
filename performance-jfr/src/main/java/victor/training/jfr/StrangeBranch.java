package victor.training.jfr;

public class StrangeBranch {

   public static void main(String[] args) {
      for (int i = 0; i < 10_000; i++) {
         unknownCode(i);
      }
   }

   private static void unknownCode(int i) {
      if (i % 10 == 0) {
         expensiveMethod();
      }
      fastMethod();
   }

   private static void fastMethod() {
      Tasks.cpu(5);
   }

   private static void expensiveMethod() {
      Tasks.cpu(100);
   }
}
