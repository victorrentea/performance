package victor.training.concurrency.leaks;

import java.util.HashMap;
import java.util.Map;

public class CachingMethodObject {
	public static class UserRightsCalculator {
		public void doStuff() {
			System.out.println("Stupid Code");
//			System.out.println("din cache direct pt ds " + cache.get("a"));
		}
	}
	
	//camp de instanta
	private Map<String, BigObject20MB> cache = new HashMap<>();
	
	public UserRightsCalculator createRightsCalculator() {
		cache.put("a", new BigObject20MB());
		cache.put("b", new BigObject20MB());
		
		return new UserRightsCalculator();
	}
}
