package victor.training.jfr;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LargeObjects {
   public static void main(String[] args) {
      List<int[]> arrays = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         arrays.add(new int[10_000_000]); // inspect TLAB allocations
      }
      System.out.println(arrays);
   }
}
