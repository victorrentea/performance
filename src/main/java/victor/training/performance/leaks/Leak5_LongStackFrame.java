package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
		// List<BigDto50Campuri> x 5.000 elem
		// List<SmallValueObject3Fields> x 100 elem
		BigObject80MB big = restCall();
		String useful = big.getInterestingPart();
//		big == null;// PAZEA: aici nu vrem sa tinem obiec...
		while (true) {
			// or wait for a loong network call, or sleep 60 sec, or deadlock
			if (useful != null) {
				log.trace("Using usefulX " + big.largeArray.length);
			}
		}
		// Conclusion?...
	}

	// supposed to return minimum data tokeep in memory.

	private BigObject80MB restCall() {
		return new BigObject80MB();
	}
}