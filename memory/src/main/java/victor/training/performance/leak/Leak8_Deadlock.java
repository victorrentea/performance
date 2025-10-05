package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak8")
public class Leak8_Deadlock {
	@GetMapping("/one")
	public String one() throws Exception {
		A.entry();
		return howtoDeadlockInstructions("two");
	}

	@GetMapping("/two")
	public String two() throws Exception {
		B.entry();
		return howtoDeadlockInstructions("one");
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

// === === === === === === === Support code  === === === === === === ===

  @GetMapping
  public String home() {
    return "call <a href='./one'>one</a> and <a href='./two'>two</a> within 3 secs..";
  }

  private String howtoDeadlockInstructions(String other) {
    return """
        Refresh the page, then <a href='%s' target='_blank'>call /%s</a> 
        within 3 seconds to produce the deadlock.<br>
        Expected Effect: both tabs hang (never finish loading).""".formatted(other,other);
  }
}





/**
 * KEY POINTS
 * ☣️ avoid 'synchronized' to guard mutable data. Prefer Map-reduce
 * ☣️ synchronized methods calling other synchronized methods - yuck
 * ☣️ Bad Design: bidirectional coupling: A->B->A !
 */
