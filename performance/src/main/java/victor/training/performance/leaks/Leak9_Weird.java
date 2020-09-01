package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

@RestController
@RequestMapping("leak9")
public class Leak9_Weird {
	@GetMapping
	public String test() {
		BigObject80MB big = new BigObject80MB();
		while (true) {
			ConcurrencyUtil.sleepq(10);
//			if (amGasitFisier) call return;
		}
		// Conclusion?...
	}
}