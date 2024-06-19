package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject80MB;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak4")
public class Leak4_LongStackFrame {
	@GetMapping
	public String endpoint() {
		String useful = adaptAnaf();
		longFlow(useful);
		return "end";
	}

	private String adaptAnaf() {
		BigObject80MB big = callAnaf();
    return big.getInterestingPart();
	}

	private void longFlow(String useful) {
		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
	}

	private @NotNull BigObject80MB callAnaf() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */