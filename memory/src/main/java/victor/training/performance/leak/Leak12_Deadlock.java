package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak12")
public class Leak12_Deadlock {

	// [RO] CATE DOI, CATE DOI ... : https://youtu.be/V798MhKfdZ8

	@GetMapping
	public String root() throws Exception {
		return "call <a href='./leak12/one'>/one</a> and <a href='./leak12/two'>/two</a> withing 3 secs..";
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

	static class KillOne {
		public static synchronized void entryPoint() {
			log("start One.a1()");
			sleepMillis(3_000);
			KillTwo.internalMethod();
			log("start One.a1()");
		}

		public static synchronized void internalMethod() {
			log("start One.b1()");
			sleepMillis(3_000);
			log("end One.b1()");
		}
	}
	static class KillTwo {
		public static synchronized void entryPoint() {
			log("start Two.a2()");
			sleepMillis(3_000);
			KillOne.internalMethod();
			log("start Two.a2()");
		}
		public static synchronized void internalMethod() {
			log("start Two.b2()");
			sleepMillis(3_000);
			log("end Two.b2()");
		}
	}

}





/**
 * KEY POINTS
 * - avoid playing with synchronized
 * - synchronized methods calling other synchronized methods - yuck
 * - Bad Design: bidirectional coupling: A->B->A !
 */
