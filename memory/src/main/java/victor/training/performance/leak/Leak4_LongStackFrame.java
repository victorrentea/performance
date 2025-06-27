package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
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
		var useful = doarCeVreau();

		log.trace("Long processing using: " + useful);
		sleepMillis(10_000); // to allow you to take a Heap Dump

		return "end";
	}

	private String doarCeVreau() {
		BigObject80MB bigDto = apiCall();
		String useful = bigDto.getInterestingPart();
		return useful;
	}

	private BigObject80MB apiCall() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */