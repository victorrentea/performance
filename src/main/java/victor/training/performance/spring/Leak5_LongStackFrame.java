package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject80MB;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
		String useful = adapter();

		futherBizLogc(useful);
		return "end";
	}

	private String adapter() {
		BigObject80MB big = fetchThePatienEHR_HL7();
		String useful = big.getInterestingPart();
		return useful;
	}

	private void futherBizLogc(String useful) {
		sleepq(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
	}

	@NotNull
	private BigObject80MB fetchThePatienEHR_HL7() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */