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
		One.entry();
		return """
        Refresh the page, then call <a href='two'>/two</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
	}

	@GetMapping("/two")
	public String two() throws Exception {
		Two.entry();
		return """
        Refresh the page, then call <a href='one'>/one</a> within 3 seconds to produce the deadlock.<br>
        Effect: requests in both tabs hang.""";
	}

	@GetMapping
	public String home() {
		return "call <a href='./one'>one</a> and <a href='./two'>two</a> within 3 secs..";
	}

	static class One {
		public static synchronized void entry() {
			log("start One.entry()");
			sleepMillis(3_000);
			Two.internal();
			log("start One.entry()");
		}

		public static synchronized void internal() {
			log("start One.internal()");
			sleepMillis(3_000);
			log("end One.internal()");
		}

	}
	static class Two {
		public static synchronized void entry() {
			log("start Two.entry()");
			sleepMillis(3_000);
			One.internal();
			log("start Two.entry()");
		}
		public static synchronized void internal() {
			log("start Two.internal()");
			sleepMillis(3_000);
			log("end Two.internal()");
		}
	}

}





/**
 * KEY POINTS
 * - avoid playing with synchronized
 * - synchronized methods calling other synchronized methods - yuck
 * - Bad Design: bidirectional coupling: A->B->A !
 */
