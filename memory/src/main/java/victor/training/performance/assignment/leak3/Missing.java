package victor.training.performance.assignment.leak3;

import victor.training.performance.util.PerformanceUtil;
import victor.training.performance.leak.obj.Big20MB;

import java.util.HashMap;
import java.util.Map;

public class Missing {
   static Map<Key, Big20MB> map = new HashMap<>();
   public static void main(String[] args) {
      for (int i = 0; i <100; i++) {
         map.put(new Key(i), new Big20MB());
         //creepy business logic
         map.remove(new Key(i));
      }

      System.out.println("Used heap: " + PerformanceUtil.getUsedHeapPretty());
      if (PerformanceUtil.getUsedHeapBytes() > 50_000_000) {
         System.err.println("GOAL NOT MET. LEAK STILL PRESENT");
      }
      System.out.println("Take a heap dump");
      PerformanceUtil.waitForEnter();
   }
}

class Key {
   private final Integer id;

   public Key(Integer id) {
      this.id = id;
   }

   public Integer getId() {
      return id;
   }

}
