package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.PerformanceUtil;

@Slf4j
@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String longRunningFunction() {
//		String useful = savingHUgeMemoryAsGCWillEvictBigObj();
		BigObject80MB big = new BigObject80MB();
		String useful = big.getInterestingPart();
		PerformanceUtil.sleepq(20000);
		System.out.println("Long logic using only " + useful + " parts of the big data");
		// Conclusion?...
		return "done";
	}

	private String savingHUgeMemoryAsGCWillEvictBigObj() {
		BigObject80MB big = new BigObject80MB();
		String useful = big.getInterestingPart();
		return useful;
	}
}