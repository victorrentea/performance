package victor.training.performance.pools.exercise;

public class Stack {
   public static void main(String[] args) {
   int x = 1;
      m(x);
   }
   public static void m(int i) {

      System.out.println("Inainte");

      System.out.println("Dupa");

//      new RuntimeException().printStackTrace();
      throw new NullPointerException();
   }
}
