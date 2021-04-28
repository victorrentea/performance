package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.PerformanceUtil;

import java.util.Random;
import java.util.function.Function;

@RestController
@RequestMapping("leak7")
public class Leak7_Java8 {
	@Autowired
	private Worker worker;

	@GetMapping
	public String test() {
		BigObject20MB big = new BigObject20MB();
		worker.workHard(new Random().nextInt(100), big::lookup);
		return "Keep calling this 5 times fast";
	}
}

@Slf4j
@Service
class Worker {
	@Async
	public void workHard(int param, Function<Integer, Integer> lookup) {
		PerformanceUtil.sleepq(10_000); // imagine other tasks doing this on the same pool
		int result = param + lookup.apply((int) Math.sqrt(param));
		log.debug("Computed " + result);
	}
}
