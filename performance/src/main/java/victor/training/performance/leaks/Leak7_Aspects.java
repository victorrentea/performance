package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("leak7")
public class Leak7_Aspects {
	@Autowired
    private  Stuff stuff;
	@GetMapping("{id}")
	public String get(@PathVariable long id) {
		stuff.stuff(id);
		return "Tools won't shield you from stupidity.";
	}
	@GetMapping("{id}/update")
	public void update(@PathVariable long id) {
		stuff.updateStuff(id, "nume nou");
	}
}

@Service
@Slf4j
class Stuff {
	@Cacheable("stuff")
	public BigObject20MB stuff(long id) {
		log.debug("Calling expensive method for id {}", id);
	    return new BigObject20MB();
	}
	@CacheEvict(cacheNames = "stuff",key = "#id")
	public void updateStuff(long id, String newName) {
		// UPDATE SET = in DB
	}
}