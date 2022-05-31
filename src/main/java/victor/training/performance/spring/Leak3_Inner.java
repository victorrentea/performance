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

	//<editor-fold desc="Entry points of more similar leaks">
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
	//</editor-fold>

	private void bizLogicUsingCalculator() {
		if (threadLocal.get().hasRight("launch")) {
			PerformanceUtil.sleepq(20_000); // long flow and/or heavy parallel load
		}
	}
}


class CachingMethodObject {

	// inner (de instanta):
//	public class UserRightsCalculator { // an instance of this is kept on current thread

	// nested (static):
	public static class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code");
//			System.out.println("am acces!!" + CachingMethodObject.this.bigMac);
			// what's the connection between this instance and the 'bigMac' field ?
			return true;
		}
	}

	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculator createRightsCalculator() { // cineva tine o ref la rezultatul acestei metode
		return new UserRightsCalculator();
	}

	// then, some more (amazing) leaks .....

	//<editor-fold desc="Lambdas vs Anonymous implementation">
	public Supplier<String> anonymousVsLambdas() {
		return () -> "Happy";
	}
	//</editor-fold>


	//<editor-fold desc="Map init in Java <= 8">
	public Map<String, Integer> mapInit() {
//		Map<String, Integer> map = new HashMap<>();
//		map.put()

//		return Map.of("one", 1, "two", 2);
		return new HashMap<>() {{ // obviously, pre-java 10
			put("one", 1);
			put("two", 2);
//			put("tot", bigMac);
		}};
	}
	//</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the parent instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state
 */