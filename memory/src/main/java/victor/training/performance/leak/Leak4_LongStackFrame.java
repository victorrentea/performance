package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    String useful = getWhatYouNeed();
//		dto = null;

		longBatch();
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return "end";
	}

	// usually placed in an adapter over the API you are consuming = architecture
	private static String getWhatYouNeed() {
		return downloadSomeData().getInterestingPart();
	}

	@NotNull
	private static BigObject80MB downloadSomeData() {
		return new BigObject80MB();
	}

	private static void longBatch() {
		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */