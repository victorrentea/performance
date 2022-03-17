package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.CachingMethodObject.UserRightsCalculator;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

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
	public static class UserRightsCalculator { // an instance of this is kept on current thread
		public boolean hasRight(String task) {
			System.out.println("Stupid Code ");
			// what's the connection with the 'bigMac' field ?
			return true;
		}
	}

	private BigObject20MB bigMac = new BigObject20MB();

	public UserRightsCalculator createRightsCalculator() { // rezultatul acestei metode este pastrat in memorie ulterior 10 min
		return new UserRightsCalculator();
	}

	// then, some more .....

	//<editor-fold desc="Lambdas vs Anonymous implementation">
	public Supplier<String> anonymousVsLambdas() {  // rezultatul acestei metode este pastrat in memorie ulterior 10 min
		return () -> "Happy da prost ca iti iei bigMac 20 MB cu tine ";
	}
	//</editor-fold>

	//<editor-fold desc="Map init in Java <= 11">
	public Map<String, Integer> mapInit() {
		return Map.of(
			"one", 1,
			"two", 2
		);
	}
	//</editor-fold>
}
