package victor.training.performance.interview;

public class Pointers {
    public static void main(String[] args) {

       m("a1");

       unreferenced();
        pointersInJava();
    }

   private static void m(String a1) {
      if (a1 == "a" + 1) {
         System.out.println("Asa");
      }
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
