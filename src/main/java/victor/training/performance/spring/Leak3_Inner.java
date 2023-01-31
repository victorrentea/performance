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

import static java.util.Map.ofEntries;

@RestController
@RequestMapping("leak3")
public class Leak3_Inner {

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
		bizLogicUsingCalculator(calculator);
		return "Done";
	}

	//<editor-fold desc="Entry points of more similar leaks">
	@GetMapping("anon")
	public String anon() {
		Supplier<String> supplier = new CachingMethodObject().anonymousVsLambdas();
		PerformanceUtil.sleepMillis(20_000); // some long workflow
		return supplier.get();
	}
	@GetMapping("map")
	public Map<String, Integer> map() {
		Map<String, Integer> map = new CachingMethodObject().mapInit();
		PerformanceUtil.sleepMillis(20_000); // some long workflow
		return map;
	}
	//</editor-fold>

	private void bizLogicUsingCalculator(UserRightsCalculator calculator) {
		if (!calculator.hasRight("launch")) {
			return;
		}
		PerformanceUtil.sleepMillis(20_000); // long flow and/or heavy parallel load
	}
}


class CachingMethodObject {
	// inner class (non-static nested classes) keeps a hard ref to the outer class
	public static class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code ce $!#&$%!^$%!^& am scris aici ");
			// what's the connection between this instance and the 'bigMac' field ?
			return true;
		}
	}

	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculator createRightsCalculator() {
		return new UserRightsCalculator();
	}

	// then, some more (amazing) leaks .....

	//<editor-fold desc="Lambdas vs Anonymous implementation">
	public Supplier<String> anonymousVsLambdas() {
		return () -> "Happy";

//		return new Supplier<String>() { // anonymous class keeps a hard ref to the outer class
//			@Override
//			public String get() {
//				return "Happy";
//			}
//		};
	}
	//</editor-fold>

	//<editor-fold desc="Map init in Java <= 8">
	public Map<String, Integer> mapInit() {
//		HashMap<String, Integer> map = new HashMap<>();
//		map.put("one", 1);
//		map.put("two", 2);
//		return map;
		// anonymous subclass of HashMap with an instance
		// initializer block inside (a kinda constructor)
		return new HashMap<>() {{ // obviously, pre-java 10
			put("one", 1);
			put("two", 2);
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