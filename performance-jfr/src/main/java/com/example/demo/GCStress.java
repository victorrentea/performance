package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GCStress {
   public static void main(String[] args) {
      List<int[]> arrays = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         arrays.add(new int[10_000_000]);
      }
      System.out.println(arrays);
      System.out.println("Press ENTER to continue...");
      new Scanner(System.in).nextLine();
   }
}
