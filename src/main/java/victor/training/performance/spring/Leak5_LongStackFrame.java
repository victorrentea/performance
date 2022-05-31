package victor.training.performance.spring;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
		String useful = frate.metCareCandIeseBigMoare();

		// biz logic care nu stie de Dto externe.
		sleepq(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return "end";
	}

	@Autowired
	private Frate frate;

}
@Component
class Frate {
	@Timed("external-call")
	// asta ar fi Adapter care sta afara din Domain:
	public String metCareCandIeseBigMoare() {
		BigObject80MB big = new BigObject80MB(); // dintr-un call extern aduc un DTO BABAN
		return big.getInterestingPart();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */