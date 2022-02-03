package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
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
		String useful = getUsefulStuff();

		sleepq(10_000); // wait for a loong network call sau procesare complicata
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return "end";
	}

	private String getUsefulStuff() {
		//de undeva de pe vreun sistem extern vine vreun DTO BLOATED cu jde'fielduri
		BigObject80MB big = new BigObject80MB();
		String useful = big.getInterestingPart();
		return useful;
	}
}