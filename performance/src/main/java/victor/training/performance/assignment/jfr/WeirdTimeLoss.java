package victor.training.performance.assignment.jfr;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static victor.training.performance.PerformanceUtil.measureCall;

public class WeirdTimeLoss {

   public static void main(String[] args) {
      System.out.println("1: " + measureCall(WeirdTimeLoss::experiment1));
      System.out.println("2: " + measureCall(WeirdTimeLoss::experiment2));
      System.out.println("3: " + measureCall(WeirdTimeLoss::experiment3));
      System.out.println("4: " + measureCall(WeirdTimeLoss::experiment4));
      System.out.println("5: " + measureCall(WeirdTimeLoss::experiment5));
      // TODO << Explain Why Experiment 5 is SO MUCH FASTER than 4
      System.out.println("6: " + measureCall(WeirdTimeLoss::experiment6));
      // Hint: compare experiment 6 with 4 using JFR

      if (measureCall(WeirdTimeLoss::experiment4) > 500) {
         System.err.println("GOAL NOT MET: experiment4 must finish under 500ms");
      }

   }

   private static boolean experiment1() {
      return logic(randomList(10_000), randomList(10_000));
   }
   private static boolean experiment2() {
      return logic(randomList(20_000), randomList(20_000));
   }
   private static boolean experiment3() {
      return logic(randomList(30_000), randomList(30_000));
   }
   private static boolean experiment4() {
      // TODO optimize this
      return logic(randomSet(30_000), randomList(30_000));
   }
   private static boolean experiment5() {
      return logic(randomSet(30_000), randomList(29_000));
   }
   private static boolean experiment6() {
      return logic(randomSet(3_000_000), randomList(2_900_000));
   }

   // DON'T TOUCH THIS:
   private static boolean logic(Collection<Integer> all, Collection<Integer> subset) {
      return all.removeAll(subset);
   }

   public static List<Integer> randomList(int size) {
      Random r = new Random();
      return IntStream.range(0, size).map(n -> r.nextInt()).boxed().collect(toList());
   }
   public static Set<Integer> randomSet(int size) {
      Random r = new Random();
      return IntStream.range(0, size).map(n -> r.nextInt()).boxed().collect(toSet());
   }

}
