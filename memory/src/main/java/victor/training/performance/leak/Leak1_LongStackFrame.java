package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big100MB;

import static victor.training.performance.util.PerformanceUtil.*;

@Slf4j
@RestController
public class Leak1_LongStackFrame {
	@GetMapping("leak1")
	public String endpoint() {
		Big100MB bigDto = apiCall();
		String a = bigDto.getA();
		String b = bigDto.getA();

    log.info("Work only using {} and {}", a, b);
		sleepSeconds(30);

		return done();
	}

	private Big100MB apiCall() {
		return new Big100MB();
	}
}

/**
 * ⭐️ KEY POINTS
 * 👍 Keep only the strictly necessary objects during longer flows
 * 👍 Call external API via an Adapter returning your own data structures
 */