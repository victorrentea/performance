package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("leak6")
public class Leak6_TheMostCommon {
	// ANATHEMA: Never bake your own cache!! Plugin a mature one!
	// [RO] Nu-ti faci cacheul la coada vacii nicioadata!
    private static Map<String, Object> oops = new HashMap<>();

	@GetMapping
	public String test() {
	    oops.put(UUID.randomUUID().toString(), new BigObject20MB());
		return "the most brainless, but most common. Long Live SonarLint";
	}
}
