package victor.training.performance.leaks;


import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleep2;

public class KillOne {
	public static synchronized void entryPoint() {
		log("start One.a1()");
		sleep2(3_000);
		KillTwo.internalMethod();
		log("start One.a1()");
	}

	public static synchronized void internalMethod() {
		log("start One.b1()");
		sleep2(3_000);
		log("end One.b1()");
	}
}






class KillTwo {
	public static synchronized void entryPoint() {
		log("start Two.a2()");
		sleep2(3_000);
		KillOne.internalMethod();
		log("start Two.a2()");
	}
	public static synchronized void internalMethod() {
		log("start Two.b2()");
		sleep2(3_000);
		log("end Two.b2()");
	}
}