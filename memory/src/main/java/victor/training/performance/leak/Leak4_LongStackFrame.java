package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big100MB;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
public class Leak4_LongStackFrame {
	@GetMapping("leak4")
	public String endpoint() {
		Big100MB bigDto = apiCall();
		String useful = bigDto.getInterestingPart();

    log.info("Processing only using a tiny {} part", useful);
		sleepMillis(10_000);
    // TODO keep less memory occupied during this flow

		return "done";
	}

	private Big100MB apiCall() {
		return new Big100MB();
	}
}

/**
 * ⭐️ KEY POINTS
 * 👍 Keep strictly necessary memory during longer flows
 * 👍 Extract useful data from large external data structures (Adapter pattern)
 */