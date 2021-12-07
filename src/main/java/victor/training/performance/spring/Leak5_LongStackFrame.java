package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject80MB;
import victor.training.performance.util.PerformanceUtil;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
		String useful = extractUsefulpart();

//		while (true) {
			// or wait for a loong network call, or sleep 60 sec, or deadlock
		PerformanceUtil.sleepq(10000);
			if (useful != null) {
				log.trace("Using useful");
			}
			return "uraaa";
//		}
		// Conclusion?...
	}

	private String extractUsefulpart() {
		BigObject80MB big = getRiskAnalisys();
		String useful = big.getInterestingPart();
		return useful;
	}

	private BigObject80MB getRiskAnalisys() {
		return new BigObject80MB();
	}
}