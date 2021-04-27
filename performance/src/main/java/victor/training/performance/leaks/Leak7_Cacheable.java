package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("leak7")
public class Leak7_Cacheable {
	@Autowired
    private  Stuff stuff;
	@GetMapping
	public String test() {
		BigObject20MB data = stuff.returnCachedDataForDay(LocalDate.now());
		return "Tools won't always shield you from mistakes: " + data;
		// but they still offer max-size, expiration..
		// https://www.ehcache.org/documentation/2.8/configuration/cache-size.html
	}
}

@Service
@Slf4j
class Stuff {
	@Cacheable("stuff")
	public BigObject20MB returnCachedDataForDay(LocalDate timestamp) {
		 log.debug("Fetch lots of data for date {}", timestamp.format(DateTimeFormatter.ISO_DATE));
	    return new BigObject20MB();
	}
}