package victor.training.concurrency.leaks;

import java.util.HashMap;
import java.util.Map;

public class CachingMethodObject {
	public static class UserRightsCalculator {
		public void doStuff() {
			System.out.println("Stupid Code");
//			cache.get("vaaai");

		}

	}
	
	private Map<String, BigObject20MB> cache = new HashMap<>();
	
	public UserRightsCalculator createRightsCalculator() {
		cache.put("a", new BigObject20MB());
		cache.put("b", new BigObject20MB());
		
		return new UserRightsCalculator();
	}
}
