package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleep2;

@RestController
@RequestMapping("leak5")
public class Leak5 {
	
	@GetMapping
	public String root() throws Exception {
		return "call <a href='./leak5/one'>/one</a> and <a href='./leak5/two'>/two</a> withing 3 secs..";
	}
	
	@GetMapping("/one")
	public String one() throws Exception {
		KillOne.entryPoint();
		return "--> You didn't call /two within the last 3 secs, didn't you?..";
	}
	
	@GetMapping("/two")
	public String two() throws Exception {
		KillTwo.entryPoint();
		return "--> You didn't call /one within the last 3 secs, didn't you?..";
	}
}



class KillOne {
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
