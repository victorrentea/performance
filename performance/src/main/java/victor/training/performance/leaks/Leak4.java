package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leaks.CachingMethodObject.UserRightsCalculator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("leak4")
public class Leak4 {
	static ThreadLocal<MyAppRequestContext> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		MyAppRequestContext requestContext = new MyAppRequestContext();
		threadLocal.set(requestContext);
		requestContext.rights = new CachingMethodObject()
				.createRightsCalculator();
		// here, no one references CachingMethodObject, but onl
		return "the most subtle";
	}
}

class MyAppRequestContext {
    public UserRightsCalculator rights;
}

class CachingMethodObject {

	public static class UserRightsCalculator { // this instance remains in memory
		public void doStuff() {
			System.out.println("Stupid Code "/* + CachingMethodObject.this.cache*/);
			// what's the connection with the 'cache' field ?
		}
	}

	private Map<String, BigObject20MB> cache = new HashMap<>(); // why does this remain in memory ?
	// because the inner class instance has a hidden reference to the outer instance

	public UserRightsCalculator createRightsCalculator() {
		cache.put("a", new BigObject20MB());
		cache.put("b", new BigObject20MB());
		return new UserRightsCalculator(); // returns a new instance
	}
}
