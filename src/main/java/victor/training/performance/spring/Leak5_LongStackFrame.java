package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject80MB;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
		BigObject80MB big = new BigObject80MB();
		String useful = big.getInterestingPart();
		while (true) {
			// or wait for a loong network call, or sleep 60 sec, or deadlock
			if (useful != null) {
				log.trace("Using useful");
			}
		}
		// Conclusion?...
	}
}