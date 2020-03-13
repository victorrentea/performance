package victor.training.performance.leaks;

import java.util.HashMap;
import java.util.Map;

public class CachingMethodObject {
	public class UserRightsCalculator {
		public void doStuff() {
			System.out.println("Stupid Code");
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
