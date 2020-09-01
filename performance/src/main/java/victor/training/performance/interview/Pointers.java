package victor.training.performance.interview;

import java.awt.*;

public class Pointers {
//   @Override
   public int hashCode() {
      return 1;
   }

   public static void main(String[] args) {

      String v = "a1";
      mmm(1, v);

      Integer i1 = 1;
      Integer i2 = 1;
      System.out.println(i1 == i2);

      Pointers p1 = new Pointers();
      Pointers p2 = p1;

      System.out.println(p1 == p2);
      System.out.println(p1.toString());
      System.out.println(System.identityHashCode(p1));


      String key = "a";
//      key = key.intern();
//      key = weakHashMap.putIfAbsent(key, key);

      unreferenced();
      pointersInJava();
   }

//   {
//      {
//         "name":"a"
//      },
//      {
//         "name":"b"
//      },
//      {
//         "name":"c"
//      },
//      {
//         "name":"d"
//      }
//   }

   private static void mmm(int i, String s2) {
      String a = "a" + i;
      String a1 = "a1";
      System.out.println(a == a1);
      System.out.println("XXX:" + (a1 == s2));
      System.identityHashCode(a1);
      System.identityHashCode(s2);
   }

   // TODO at which point can the instance be GC-ed ?
   private static void unreferenced() {
      Pointers p = new Pointers();
      Pointers p2 = p;
      // A
      p2 = null;
      // B
      p = null;
      // C
   }

   private static void pointersInJava() {
      Pointers a = new Pointers();
      Pointers b = new Pointers();
      Pointers c = a;
      checkIdentity(a, b, c);

      // TODO play checkEquals("a1", "a1", "a1");

   }

   private static void checkIdentity(Object x, Object a, Object b) {
      System.out.println("\nCheck equals class " + x.getClass());
      System.out.println("a==b: " + (x == a));
      System.out.println("a==c: " + (x == b));

      System.out.printf("toString()\n\ta=%s\n\tb=%s\n\tc=%s\n", x, a, b);
      System.out.printf("hashCode()\n\ta=%d\n\tb=%d\n\tc=%d\n", x.hashCode(), a.hashCode(), b.hashCode());
      // TODO change to %x
      // TODO change to identityHashCode
   }

}
