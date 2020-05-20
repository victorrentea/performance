package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("leak2")
public class Leak2 {
	static ThreadLocal<MyAppRequestContext> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		MyAppRequestContext requestContext = new MyAppRequestContext();
		threadLocal.set(requestContext);
		requestContext.rights = new CachingMethodObject()
				.createRightsCalculator();
		return "subtle";
	}
}
class MyAppRequestContext {
    public CachingMethodObject.UserRightsCalculator rights;
}
class CachingMethodObject {
	public static class UserRightsCalculator {
		public void doStuff() {
			System.out.println("Stupid Code" /* + cache.keySet()*/);

			// what's the connection with the 'cache' field ?
		}
	}

	private Map<String, BigObject20MB> cache = new HashMap<>();

	public UserRightsCalculator createRightsCalculator() {
		cache.put("a", new BigObject20MB());
		cache.put("b", new BigObject20MB());
		return new UserRightsCalculator(); // returns a new instance
	}
}
