package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("leak9")
public class Leak9_Java8 {

	ExecutorService pool = Executors.newFixedThreadPool(2); //

	@GetMapping
	public String test() {
		BigObject20MB big = new BigObject20MB();

		pool.submit(() -> workHard(new Random().nextInt(100), index -> big.lookup(index)));
		return "Keep calling this 5 times fast";
	}
	public void workHard(int param, Function<Integer, Integer> lookup) {
		ConcurrencyUtil.sleepq(10_000); // imagine other tasks doing this on the same pool
		int result = param + lookup.apply((int) Math.sqrt(param));
		log.debug("Computed " + result);
	}
}

