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
		String usefulTinyBit = metodaDintrunAdapterCareNUlasaInCoreSaIntreDtoDusman();

		sleepq(200); // start a long-running process (eg 20 minutes)
		if (usefulTinyBit != null) {
			log.trace("Using useful part: " + usefulTinyBit);
		}
		return "end";
	}

	private String metodaDintrunAdapterCareNUlasaInCoreSaIntreDtoDusman() {
		BigObject80MB big = retrieveDtoDeLaUnSistemExtern();
		String usefulTinyBit = big.getInterestingPart();
		return usefulTinyBit;
	}

	@NotNull
	private BigObject80MB retrieveDtoDeLaUnSistemExtern() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */