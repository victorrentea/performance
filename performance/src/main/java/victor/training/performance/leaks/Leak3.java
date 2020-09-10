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
@RequestMapping("leak3")
public class Leak3 {
	@Autowired
    private UserContext userData;
	
	@GetMapping
	public String test() throws Exception {
		String uuid = UUID.randomUUID().toString(); // this makes that the cache never has cahce hit. always is missed and keeps growing
		userData.tryCache(uuid, BigObject20MB::new);
		return "even more subtle, know your frameworks!";
	}
}

//<bean scope="session"
@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext implements Serializable { // this object remains in user session

	private Map<String, BigObject20MB> cache = new HashMap<>(); // not invented here Syndrome

	public BigObject20MB tryCache(String key, Callable<BigObject20MB> loadMethod) throws Exception {
		if (cache.containsKey(key)) {
			return cache.get(key); // cache hit
		}
		BigObject20MB newObject = loadMethod.call();
		cache.put(key, newObject);
		return newObject;
	}
}
