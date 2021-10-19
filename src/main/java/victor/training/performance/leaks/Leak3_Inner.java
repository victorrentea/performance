package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.PerformanceUtil;
import victor.training.performance.leaks.CachingMethodObject.UserRightsCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("leak3")
public class Leak3_Inner {
	public static ThreadLocal<UserRightsCalculator> threadLocal = new ThreadLocal<>();
	public static ThreadLocal<Supplier<Integer>> supplierOnThreadLocal = new ThreadLocal<>();

	@GetMapping
	public String test() {
		UserRightsCalculator calculator = new CachingMethodObject().createRightsCalculator();
		threadLocal.set(calculator);
		supplierOnThreadLocal.set(new CachingMethodObject().createRightsCalculatorSupplier());
		try {
			bizLogicUsingCalculator();
		} finally {
			threadLocal.remove();
			supplierOnThreadLocal.remove();
		}
		return "Do you know Java?";
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
			System.out.println("Stupid Code");
			// what's the connection with the 'bigMac' field ?
//			System.out.println(bigMac);
			return true;
		}
	}

	private Map<String, BigObject20MB> bigMac = new HashMap<>();

	public UserRightsCalculator createRightsCalculator() {
		bigMac.put("a", new BigObject20MB());
		bigMac.put("b", new BigObject20MB());
		return new UserRightsCalculator();
	}

	public Supplier<Integer> createRightsCalculatorSupplier() {
		Map<String, BigObject20MB> bigMacX = new HashMap<>();

		bigMac.put("c", new BigObject20MB());
		bigMac.put("d", new BigObject20MB());
		bigMacX.put("clocal", new BigObject20MB());
		bigMacX.put("dlocal", new BigObject20MB());
		return new Supplier<Integer>() {
			@Override
			public Integer get() {
//				System.out.println(bigMac);
//				System.out.println(bigMacX);
				return 1;
			}
		};
	}
}
