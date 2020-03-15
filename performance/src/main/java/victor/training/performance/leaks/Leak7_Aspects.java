package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("leak7")
public class Leak7_Aspects {
	@Autowired
    private  Stuff stuff;
	@GetMapping
	public String test() {
		stuff.stuff(LocalDateTime.now().toString());
		return "the most brainless, but most common. Long Live SonarLint";
	}
}

@Service
@Slf4j
class Stuff {
	@Cacheable("big")
	public BigObject20MB stuff(String timestamp) {
		log.debug("Calling method for {}", timestamp);
	    return new BigObject20MB();
	}
}