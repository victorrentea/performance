package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;
import victor.training.performance.spring.CachingMethodObject.UserRightsCalculator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("leak3")
public class Leak3_Inner {
	public static ThreadLocal<UserRightsCalculator> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		UserRightsCalculator calculator = new CachingMethodObject().createRightsCalculator();
		threadLocal.set(calculator);
		bizLogicUsingCalculator();
		return "Do you know Java?";
	}

	private void bizLogicUsingCalculator() {
		if (threadLocal.get().hasRight("launch")) {
			PerformanceUtil.sleepq(20_000); // long flow and/or heavy parallel load
		}
	}
}


class CachingMethodObject {
	public class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code");
			// what's the connection with the 'bigMac' field ?
			return true;
		}
	}

	private Map<String, BigObject20MB> bigMac = new HashMap<>();

	public UserRightsCalculator createRightsCalculator() {
		bigMac.put("a", new BigObject20MB());
		bigMac.put("b", new BigObject20MB());
		return new UserRightsCalculator();
	}
}
