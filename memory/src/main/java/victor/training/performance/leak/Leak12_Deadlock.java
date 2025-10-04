package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak12")
public class Leak12_Deadlock {
	@GetMapping("/one")
	public String one() throws Exception {
		A.entry();
		return """
        Refresh the page, then call <a href='two'>/two</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
	}

	@GetMapping("/two")
	public String two() throws Exception {
		B.entry();
		return """
        Refresh the page, then call <a href='one'>/one</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
	}

	@GetMapping
	public String home() {
		return "call <a href='./one'>one</a> and <a href='./two'>two</a> within 3 secs..";
	}

	static class A {
		public static synchronized void entry() {
			log("Entered A.entry()");
			sleepMillis(3_000);
			B.internal();
			log("Exiting A.entry()");
		}

		public static synchronized void internal() {
			log("Entered A.internal()");
			sleepMillis(3_000);
			log("Exiting A.internal()");
		}
	}




	static class B {
		public static synchronized void entry() {
			log("Entered B.entry()");
			sleepMillis(3_000);
			A.internal();
			log("Exiting B.entry()");
		}

		public static synchronized void internal() {
			log("Entered B.internal()");
			sleepMillis(3_000);
			log("Exiting B.internal()");
		}
	}
}





/**
 * KEY POINTS
 * - avoid playing with synchronized
 * - synchronized methods calling other synchronized methods - yuck
 * - Bad Design: bidirectional coupling: A->B->A !
 */
