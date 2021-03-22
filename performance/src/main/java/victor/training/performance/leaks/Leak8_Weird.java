package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak8")
public class Leak8_Weird {
	public int method() {

		if (true) {
			throw new IllegalArgumentException();
		}

		return 1;
	}
	@GetMapping
	public String test() {
		BigObject80MB big = new BigObject80MB();
		String utile = "big. --- > 1kb de date utile";

		while (true) ; // or wait for a loong network call, or sleep 60 sec, or deadlock
		// Conclusion?...
	}
}