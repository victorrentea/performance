package victor.training.performance.jfr.tlab;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;

public class Garbage {
   public static void main(String[] args) {

//      HttpServletRequest r;
//      r.getReader().lines()
//          .forEach(System.out::println);


//      HttpServletResponse r;
//      r.getWriter().write(s);

      System.out.println("[ENTER] when ready");
      new Scanner(System.in).nextLine();
      System.out.println("Start...");
      StringBuilder s = new StringBuilder();
      new StringBuffer(s.toString());
      for (int i = 0; i < 300_000; i++) {
         s.append(i).append("X");
//         s = s + i + "X";
//         s = new StringBuilder(s.length() + 50).append(s).append(i).append("X").toString();
         if (i % 1000 == 0) {
            System.out.println(s.length());
         }
      }
      System.out.println(s.length());
      new Scanner(System.in).nextLine();
   }
}
