package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("leak7")
public class Leak7_Cacheable {
	@Autowired
    private  Stuff stuff;
	@GetMapping
	public String test() {
		BigObject20MB data = stuff.stuff(LocalDate.now().toString());

		return "Tools won't shield you from stupidity. " + data;
	}
	@GetMapping("clear")
	public void clearCache() {
		stuff.clearCache(LocalDate.now().toString());
	}
}

@Service
@Slf4j
class Stuff {
	@Cacheable("stuff")
	public BigObject20MB stuff(String timestamp) {
		log.debug("Calling method for {}", timestamp);
	    return new BigObject20MB();
	}

	@CacheEvict("stuff")
	public void clearCache(String timestamp) {
	}
}