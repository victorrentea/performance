package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

import java.util.Random;
import java.util.function.Function;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@RequestMapping("leak6")
public class Leak6_Async {
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
		log.debug("Starting to work hard...");
		sleepq(10_000);
		int result = param + lookup.apply((int) Math.sqrt(param));
		log.debug("Done task. result=" + result);
	}
}

/**
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */