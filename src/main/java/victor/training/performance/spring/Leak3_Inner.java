package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.CachingMethodObjectHeavy.UserRightsCalculatorFMic;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
		UserRightsCalculatorFMic calculator = new CachingMethodObjectHeavy().createRightsCalculator();
		// aici daca vine GC poate elibera intanta de CachingMethodObject ?
		bizLogicUsingCalculator(calculator);
		return "Done";
	}

	//<editor-fold desc="Entry points of more similar leaks">
	@GetMapping("anon")
	public String anon() {
		Supplier<String> supplier = new CachingMethodObjectHeavy().anonymousVsLambdas();
		PerformanceUtil.sleepq(20_000); // some long workflow
		return supplier.get();
	}
	@GetMapping("map")
	public Map<String, Integer> map() {
		Map<String, Integer> map = new CachingMethodObjectHeavy().mapInit();
		PerformanceUtil.sleepq(20_000); // some long workflow
		return map;
	}
	//</editor-fold>

	private void bizLogicUsingCalculator(UserRightsCalculatorFMic calculator) {
		if (!calculator.hasRight("launch")) {
			return;
		}
		PerformanceUtil.sleepq(20_000); // long flow and/or heavy parallel load
	}
}


class CachingMethodObjectHeavy {
	// 2 moduri de a pune clase-n clase:
	// nested aka static
	// inner nestatica
	public static class UserRightsCalculatorFMic { // an instance of this is kept on current thread
		// nici un camp. ca atare nu ar trebui sa tina in viata nimic altceva
		public boolean hasRight(String task) {
			System.out.println("Stupid Code ");
			// what's the connection between this instance and the 'bigMac' field ?
			return true;
		}
	}



	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculatorFMic createRightsCalculator() {
		return new UserRightsCalculatorFMic();
	}

	// then, some more (amazing) leaks .....

	//<editor-fold desc="Lambdas vs Anonymous implementation">
	public Supplier<String> anonymousVsLambdas() {
		return () -> "Happy";
//		return new Supplier<String>() { // tine ref strong la ob din jur
//			@Override
//			public String get() {
//				return "Happy";
//			}
//		};
	}
	//</editor-fold>

	//<editor-fold desc="Map init in Java <= 8">
	public Map<String, Integer> mapInit() {
//		return new HashMap<>() { // sublcasa de HashMap
//			{ // bloc de init de instanta
//				// obviously, pre-java 10 modul lenesului de a initializa o mapa cu min taste apasate
//				put("one", 1);
//				put("two", 2);
//				System.out.println("Oups! sublcasa anonima de hashMap tine ref la instanta din jur" + bigMac);
//			}};
		// java 8
//		Map<String, Integer> map = new HashMap<>();
//		map.put("one",1);
//		map.put("two",2);
//		return map;

		//java 10
		return Map.of(
				 "one",1,
				 "two",2);
	}
	//</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the host instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state in fields
 */