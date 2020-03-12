package victor.training.concurrency.interview;

public class Pointeri {

	
	public static void main(String[] args) {
		String [] a = {};
		
		System.out.println(a);
		String s = "a" + 1;
		
		String s2 = "a1";
		System.out.println(new Pointeri());
		System.out.println(System.identityHashCode(s));
		System.out.println(System.identityHashCode(s2));
		Pointeri p = new Pointeri();
		Pointeri p2 = p;
		//aici
		p2 = null; // inca nu
		p = null; // acum o poate curata
		System.out.println(s == s2);
	}
}
