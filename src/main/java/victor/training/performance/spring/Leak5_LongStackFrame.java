package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject80MB;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	@Async
	public CompletableFuture<String> longRunningFunction() {
		String useful = doarCeVreau();

		sleepMillis(10_000); // start a long-running process (eg 20 minutes)
		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
		return CompletableFuture.completedFuture("end");
	}

	private static String doarCeVreau() {
		BigObject80MB big = fetch();
		return big.getInterestingPart();
	}

	@NotNull
	private static BigObject80MB fetch() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */