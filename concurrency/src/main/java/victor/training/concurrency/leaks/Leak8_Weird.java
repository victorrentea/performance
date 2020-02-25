package victor.training.concurrency.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("leak8")
public class Leak8_Weird {
	@GetMapping
	public String test() {
		BigObject80MB big = new BigObject80MB();
		while (true) ;
	}
}
