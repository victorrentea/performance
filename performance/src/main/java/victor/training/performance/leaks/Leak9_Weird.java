package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak9")
public class Leak9_Weird {
	@GetMapping
	public String test() {
		BigObject80MB big = new BigObject80MB();
		while (true) ; // or sleep 60 sec or deadlock
		// Conclusion?...
		// nu tine obiecte mari in memorie pentru mult timp, ci strict cat e necesar.
	}
}