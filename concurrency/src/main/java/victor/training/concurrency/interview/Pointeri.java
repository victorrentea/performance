package victor.training.concurrency.interview;


import java.io.File;
import java.nio.file.Files;
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

	
	public static void main(String[] args) {

		A a = new A();
		A a2 = new A();


		System.out.println(a == a2);

		System.out.println(a.hashCode());
		System.out.println(a2.hashCode());

		System.out.println(System.identityHashCode(a));
		System.out.println(System.identityHashCode(a2));


	}
}
