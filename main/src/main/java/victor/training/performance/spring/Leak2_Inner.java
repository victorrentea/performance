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
@RequestMapping("leak2")
public class Leak2_Inner {

	@GetMapping
	public String home() {
		return "Do you know Java?<br>" +
			   "<li><a href='/leak2/inner'>Inner class</a>" +
			   "<li><a href='/leak2/anon'>Lambdas vs anonymous class</a>" +
			   "<li><a href='/leak2/map'>Map{{</a> ";
	}
	@GetMapping("inner")
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
	public class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code");
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
		return new Supplier<String>() {
			@Override
			public String get() {
				return "Happy";
			}
		};
	}
	//</editor-fold>

	//<editor-fold desc="Map init in Java <= 8">
	public Map<String, Integer> mapInit() {
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