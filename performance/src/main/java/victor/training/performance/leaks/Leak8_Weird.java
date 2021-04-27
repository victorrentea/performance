package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak8")
public class Leak8_Weird {
	@GetMapping
	public String test() {
		String date = loadInitialData();
		while (true) ; // or wait for a loong network call, or sleep 60 sec, or deadlock
		// Conclusion?...
	}

	private String loadInitialData() {
		BigObject80MB big = new BigObject80MB();
		System.out.println("Calcule cu " + big + " din care rezulta doar putine date EFECTIV de tinut minte pentru restul fluxului de 2h care incepe mai jos.");
		return "date utile";
	}
}