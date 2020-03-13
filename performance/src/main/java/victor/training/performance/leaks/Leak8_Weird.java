package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak8")
public class Leak8_Weird {
	@GetMapping
	public String test() {
		BigObject80MB big = new BigObject80MB();
		while (true) ; // or sleep 60 sec
		// Conclusion?...
	}
}