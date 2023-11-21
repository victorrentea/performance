package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import victor.training.performance.util.BigObject20MB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak5")
public class Leak5_AsyncUnbounded {
	@Autowired
	private Worker worker;

	@GetMapping
	public String test(/*fisier*/MultipartFile uploadedFile) throws IOException {
		byte[] bytes = uploadedFile.getBytes(); // intr-o zi o sa fie mare -> OutOfMemory

		File tempFile = File.createTempFile("date", "dat");
		try (var fos = new FileOutputStream(tempFile)) {
			IOUtils.copy(uploadedFile.getInputStream(), fos, 10_000); // daca clientul impinge date pe retea mai repede
			// decat apuci tu sa scrii pe SSD
			// mai e solutie folosind DMA expuse prin Java NIO Channel: mufezi direct placa de retea la SSD fara sa treaca prin RAM, doar prin DMA
		}
		// sau tragi tu un request baban de undeva ...
//		byte[] TODO
		BigObject20MB big = new BigObject20MB(); // le primesc de pe request, eg fiser de import
		worker.workHard(new Random().nextInt(100), big::lookup, tempFile);
		return "Keep calling this 20 times within 10 seconds, then heap dump";
	}

}

@Slf4j
@Service
class Worker {
	@Async // ruleaza pe alt thread functia asta. poate sta la coada cand n-ai threaduri.
	// @Async by default ruleaza pe un thread pool de 8 threaduri default in Spring
	public void workHard(int param, Function<Integer, Integer> lookup, File bytes) {
		log.debug("Starting to work hard...");
		sleepMillis(10_000);
		int result = param + lookup.apply((int) Math.sqrt(param));
		log.debug("Done task. result=" + result);
	}
}

/**
 * KEY POINTS
 * - Don't pass large objects as params to async methods
 * - Tune the queue size of the underlying thread pool considering the size of elements
 */