package victor.training.jfr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
@SpringBootApplication
@EnableMBeanExport
public class DemoApplication {
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	@Scheduled(fixedRate = 1000)
	public void m() {
	    log.debug("Stuff");
	}
	@Value("${minWaitThresholdForLogging}")
	private long minWaitThresholdForLogging;

	@Bean
	public ThreadPoolTaskExecutor importThreadPool(@Value("${import.pool.size}") int importPoolSize) {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setMaxPoolSize(importPoolSize);
		pool.setCorePoolSize(importPoolSize);
		pool.setTaskDecorator(new TaskDecorator() {
			@Override
			public Runnable decorate(Runnable original) {
				log.info("Decorating task running in the thread that does .submit()");
				long submitTime  = System.currentTimeMillis();
				return () -> {
					long startTime = System.currentTimeMillis();
					if (startTime - submitTime > minWaitThresholdForLogging) {
						log.info("Waited in queue {} seconds" , (startTime-submitTime)/1000);
					}
					log.info("Start task - works on worker thread");
					original.run();
				};
			}
		});
		return pool;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

class DataStructure {}

@Component
class FileProcessor {
	private static final Logger log = LoggerFactory.getLogger(FileProcessor.class);

	public void processFile(int fileId) {
		log.info("Start processing file {}", fileId);
		ThreadUtils.sleepq(5_000); //network
		Tasks.cpu(5_000);
		log.info("End {}", fileId);
	}
}

@RestController
class R1 {
	AtomicInteger atomicInteger = new AtomicInteger(0);
	private static final Logger log = LoggerFactory.getLogger(R1.class);
	@Autowired
	private ThreadPoolTaskExecutor importThreadPool;
	@Autowired
	private FileProcessor fileProcessor;

	@GetMapping("file")
	public String startProcessingFile() {
		int fileId = atomicInteger.incrementAndGet();

		if (log.isDebugEnabled()) {
			log.debug("Start {}", convertToJson(new DataStructure()));
		}
		log.info("Submitting file {}", fileId);
		long sumbmitTime = System.currentTimeMillis();
		importThreadPool.submit(() -> fileProcessor.processFile(fileId));
		return "Done";
	}

	private String convertToJson(DataStructure dataStructure) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "somthething which in prod is not really logged anywhere";
	}

	@GetMapping("jfr")
	public String hello() throws InterruptedException {
		CheckStockEvent event = new CheckStockEvent();
		event.begin();

		int[] ints = new int[1000];
		System.out.println(ints.length);

		Thread.sleep(1000);
		if (event.isEnabled()) {
			event.commit();
		}
		return "Hello";
	}
}
