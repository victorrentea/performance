package victor.training.concurrency.interview;


import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class A {
	@Override
	public String toString() {
		return "A frate";
	}

	@Override
	public int hashCode() {
		return 1;
	}
}

public class Pointeri {


	private static A adetot;
	public static final Map<Long, Object> toataBaza = new HashMap<>();


	public static void main(String[] args) {

		m();
		// aici


	}

	private static void m() {
		A a = new A();

		String s ="";
		s+= a;
		s+= a;
		s+= a;
		adetot = a;
		// 1 ref
		// AICI intra GC
		A abis = a;
		// 2 ref
		a = null;
//		1 ref
		abis = null;
		// 0 ref

		A a2 = new A();
		System.out.println(a == a2);

		System.out.println(a.hashCode());
		System.out.println(a2.hashCode());

		System.out.println(System.identityHashCode(a));
		System.out.println(System.identityHashCode(a2));
	}
}
