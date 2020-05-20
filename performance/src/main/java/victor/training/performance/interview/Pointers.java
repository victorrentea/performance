package victor.training.performance.interview;

public class Pointers {
    public static void main(String[] args) {
        unreferenced();
        pointersInJava();
    }

    // TODO at which point can the instance be GC-ed ?
    private static void unreferenced() {
        Pointers p = new Pointers(); // 1
        // -- p este legat de un stack frame
        Pointers p2 = p; //2
        // A
        p2 = null; //1
        // B
        p = null; // 0
        // C
    }

    private static void pointersInJava() {
        Pointers a = new Pointers(); // 1
        Pointers b = new Pointers();
        Pointers c = a; // 2
		checkIdentity(a, b, c);

        String ceva = "a";
        checkIdentity("a1", "a1", ceva + "1");

    }

	private static void checkIdentity(Object a, Object b, Object c) {
		System.out.println("\nCheck equals class " + a.getClass());
		System.out.println("a==b: " + (a == b));
		System.out.println("a==c: " + (a == c));

		System.out.printf("toString()\n\ta=%s\n\tb=%s\n\tc=%s\n", a, b, c);

		System.out.printf("hashCode()\n\ta=%x\n\tb=%x\n\tc=%x\n",
                a.hashCode(), b.hashCode(), c.hashCode());

		System.out.printf("identityHashCode()\n\ta=%x\n\tb=%x\n\tc=%x\n",
                System.identityHashCode(a),
                System.identityHashCode(b),
                System.identityHashCode(c));

		// TODO change to %x
		// TODO change to identityHashCode
	}

    @Override
    public int hashCode() {
        return 9;
    }

    @Override
    public String toString() {
        return "axxx";
    }
}
