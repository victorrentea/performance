package victor.training.performance.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;


@RestController
@RequestMapping("profile/tlab")
public class Profile9_TLAB {
   public static void main(String[] args) {
      System.out.println("[ENTER] when ready");
      new Scanner(System.in).nextLine();
      System.out.println("Start...");
      String s = new Profile9_TLAB().stringConcat();
      System.out.println(s.length());
   }

   @GetMapping
   private String stringConcat() {
      String s = "";
      for (int i = 0; i < 300_000; i++) {
         s += i + " ";
         if (i % 1000 == 0) {
            System.out.println(i);
         }
      }
      return "Composed a string of size: " + s.length();
   }
}
