package victor.training.jfr;

import java.util.Scanner;

public class Garbage {
   public static void main(String[] args) {
      System.out.println("[ENTER] when ready");
      new Scanner(System.in).nextLine();
      System.out.println("Start...");
      String s = "";
      for (int i = 0; i < 300_000; i++) {
         s += i + " ";
         if (i % 1000 == 0) {
            System.out.println(i);
         }
      }
      System.out.println(s.length());
   }
}
