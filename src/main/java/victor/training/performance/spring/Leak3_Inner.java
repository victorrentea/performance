package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.CachingMethodObject.UserRightsCalculator;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("leak3")
public class Leak3_Inner {
	public static final ThreadLocal<UserRightsCalculator> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String home() {
		return "Do you know Java? <br>If you think you do:<br>" +
				 "<li>Start here: <a href='/leak3/puzzle'>puzzle</a> (pauses 20 sec to get a heap dump)" +
				 "<li><a href='/leak3/anon'>anon</a>" +
				 "<li><a href='/leak3/map'>map</a> ";
	}
	@GetMapping("puzzle")
	public String puzzle() {
		UserRightsCalculator calculator = new CachingMethodObject().createRightsCalculator();
		threadLocal.set(calculator);
		bizLogicUsingCalculator();
		return "Done";
	}
	@GetMapping("anon")
	public String anon() {
		Supplier<String> supplier = new CachingMethodObject().anonymousVsLambdas();
		PerformanceUtil.sleepq(20_000); // some long workflow
		return supplier.get();
	}
	@GetMapping("map")
	public Map<String, Integer> map() {
		Map<String, Integer> map = new CachingMethodObject().mapInit();
		PerformanceUtil.sleepq(20_000); // some long workflow
		return map;
	}

	private void bizLogicUsingCalculator() {
		if (threadLocal.get().hasRight("launch")) {
			PerformanceUtil.sleepq(20_000); // long flow and/or heavy parallel load
		}
	}
}


class CachingMethodObject {
//	public class UserRightsCalculator { //inner class instanta de din asta pointer ascuns la instanta clasei host
	public static class UserRightsCalculator { // nested
		public boolean hasRight(String task) {
			System.out.println("Stupid Code");
			// what's the connection with the 'bigMac' field ?
//			System.out.println(CachingMethodObject.this.bigMac);
			return true;
		}
	}

	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculator createRightsCalculator() {
		return new UserRightsCalculator();
	}

	// then, some more .....

	//<editor-fold desc="More">
	public Supplier<String> anonymousVsLambdas() {
		return new Supplier<String>() {
			@Override
			public String get() {
				return "Happy";
			}
		};
	}


	public Map<String, Integer> mapInit() {
		return new HashMap<>() {{ // obviously, pre-java 10 Map.of(...)
			put("one", 1);
			put("two", 2);
		}};
	}
	//</editor-fold>
}
