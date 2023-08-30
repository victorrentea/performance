package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
		String useful = adapterDesignPattern(); // si mai curat, si mai performant
		// 🛑 don't reference large objects longer than needed

		bizLogicHOrrorJDemiiDeLinii(useful);
		return "end";
	}

	private static String adapterDesignPattern() {
		// gunoiul "lor" ramane ascuns fata de restul codului
		BigObject80MB huge = restCall(); // sta pe stiva pe toata durata bizLogic secunde
		return huge.getInterestingPart();
	}

	private static void bizLogicHOrrorJDemiiDeLinii(String useful) {
		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
	}

	@NotNull
	private static BigObject80MB restCall() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */