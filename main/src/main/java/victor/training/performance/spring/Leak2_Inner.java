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


class CachingMethodObject { // asta credeam ca se GC
//	public  class UserRightsCalculator { // inner RAU
//	public static class UserRightsCalculator { // nested

	public static class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code " );
			// what's the connection between this instance and the 'bigMac' field ?
			// 🛑 careful with hidden links
			return true;
		}
	}

//	{
//		System.out.println("asta?");
//	}

	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculator createRightsCalculator() {
		return new UserRightsCalculator();
	}

	// then, some more (amazing) leaks .....

	//<editor-fold desc="Lambdas vs Anonymous implementation">
	public Supplier<String> anonymousVsLambdas() {
		return () -> "Happy"; // nu tine ref la clasa din jur decat daca javac te vede
		// ca folosesti efectiv ceva din jur

//		return new Supplier<String>() { // anonymous class implem an interface
//			@Override
//			public String get() {
//				return "Sad";
//			}
//		};
	}
	//</editor-fold>

	//<editor-fold desc="Map init in Java <= 8">
	public Map<String, Integer> mapInit() {

		 // rau, facea lumea de LENE, ca sanu repete map.put ci doar put < dobitoci
//		return new HashMap<>() { // subclasa tine ref la instanta din jur
//			{ // ~ constructor
//				// obviously, pre-java 10
//			put("one", 1);
//			put("two", 2);
//		}};
		return Map.of("one", 1, "two", 2);
	}
	//</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the parent instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state
 */