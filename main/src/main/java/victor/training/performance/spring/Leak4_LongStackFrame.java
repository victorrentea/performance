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
		String useful = adapterDesignPattern(); // te apara de ANAF
		// 🛑 don't reference large objects longer than needed
//		big = null;// Doamne fereste, ca nimeni nu intelege de ce faci asta


		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return "end";
	}

	private String adapterDesignPattern() {
		BigObject80MB big = new BigObject80MB(); // pp ca a venit de la ANAF un jsonel
		String useful = big.getInterestingPart(); // 200 bytes
		return useful;
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */