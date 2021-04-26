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
		requestContext.rights = new CachingMethodObject().createRightsCalculator();
		// aici obiectul CachingMethodObject poate disparea din mem la GC
		threadLocal.set(requestContext);
		try {
			metDeBiz();
		} finally {
			threadLocal.remove();
		}
		return "the most subtle";
	}

	private void metDeBiz() {
	}
}

class MyAppRequestContext {
    public UserRightsCalculator rights;
}

class CachingMethodObject { // 40 MB
	static public class UserRightsCalculator { // nested
		public void doStuff() {
			System.out.println("Stupid Code ");
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
