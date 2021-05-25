package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak5")
public class Leak5_LongStackFrame {
	@GetMapping
	public String test() {
		String whatIActuallUseFromBig = extractMinumumData();

		System.out.println(whatIActuallUseFromBig);
		while (true) ; // or wait for a loong network call, or sleep 60 sec, or deadlock
		// Conclusion?...
	}

	private String extractMinumumData() {
		BigObject80MB big = new BigObject80MB();
		String whatIActuallUseFromBig = extract(big);
		return whatIActuallUseFromBig;
	}

	private String extract(BigObject80MB wholeData) {
		return "My useful data: " + wholeData.date;
	}
}