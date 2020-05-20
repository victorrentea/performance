package victor.training.performance.leaks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;


@RestController
@RequestMapping("leak4")
public class Leak4 {
	@Autowired
    private UserContext userData;
	
	@GetMapping
	public String test() throws Exception {
		String uuid = UUID.randomUUID().toString();
		userData.tryCache(uuid, BigObject20MB::new);
		return "the most subtle";
	}
}

@Component
// NU PUI CA PORCU pe sesiune.
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext implements Serializable {

	private Map<String, BigObject20MB> cache = new HashMap<>();

	// un cache ca asta nu-ti va mai aduce ca$h
	public BigObject20MB tryCache(String key, Callable<BigObject20MB> loadMethod) throws Exception {
		if (cache.containsKey(key)) {
			return cache.get(key); // cache hit
		}
		BigObject20MB newObject = loadMethod.call();
		cache.put(key, newObject);
		return newObject;
	}
}
