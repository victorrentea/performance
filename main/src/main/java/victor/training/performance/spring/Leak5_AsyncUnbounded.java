package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak5")
public class Leak5_AsyncUnbounded {
	@Autowired
	private Worker worker;
	private static final AtomicInteger index = new AtomicInteger(0);

	@GetMapping
	public String endpoint(@RequestParam(value = "file",required = false)
													 MultipartFile multipartFile) throws IOException {
		byte[] contents = multipartFile.getBytes();
		worker.processFile(index.incrementAndGet(), contents);
		return "Keep calling this 20 times within 10 seconds, then heap dump";
	}
}

@Slf4j
@Service
class Worker {
	@Async // runs on a default Spring thread pool with 8 threads
	public void processFile(int param, byte[] contents) {
		log.debug("Start task...");
		sleepMillis(10_000);
		int count = (int) IntStream.range(0, contents.length).filter(i -> contents[i] == 17).count();
    log.debug("Done task");
	}
}

/**
 * KEY POINTS
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */