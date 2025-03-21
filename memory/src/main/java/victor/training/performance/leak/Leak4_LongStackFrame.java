package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject80MB;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak4")
public class Leak4_LongStackFrame {
	private static final ExecutorService myOwnExecutor = Executors.newFixedThreadPool(1);
	@GetMapping
	public String endpoint() {
		CompletableFuture.runAsync(()->longWork(), myOwnExecutor);
		return "end";
	}

	private void longWork() {
		String useful = fetchWhatINeed();

		sleepMillis(10_000); // imagine a long-running process (eg minutes...)

		if (useful != null) {
			log.trace("Using useful part: " + useful);
		}
	}

	private String fetchWhatINeed() {
		BigObject80MB big = apiCall(); // DTO
    return big.getInterestingPart();
	}

	private BigObject80MB apiCall() {
		return new BigObject80MB();
	}
}

/**
 * KEY POINTS
 * - Don't keep large objects in local variables of long-running functions
 */