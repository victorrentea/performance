package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject80MB;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak4")
public class Leak4_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
		String useful = fetchStuff();
		// 🛑 don't reference large objects longer than needed

		apiCall();
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return "end";
	}

	private static String fetchStuff() {
		BigObject80MB big = new BigObject80MB();
		return big.getInterestingPart();
	}

	private static void apiCall() {
		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */