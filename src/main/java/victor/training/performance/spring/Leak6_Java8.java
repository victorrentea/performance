package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.util.Random;
import java.util.function.Function;

@RestController
@RequestMapping("leak6")
public class Leak6_Java8 {
	@Autowired
	private Worker worker;

	@GetMapping
	public String test() {
		BigObject20MB big = new BigObject20MB();
		worker.workHard(new Random().nextInt(100), big::lookup);
		return "Keep calling this 20 times within 10 seconds";
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
