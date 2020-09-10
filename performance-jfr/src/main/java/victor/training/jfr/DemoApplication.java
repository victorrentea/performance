package victor.training.jfr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@SpringBootApplication
@EnableMBeanExport
public class DemoApplication {
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	@Scheduled(fixedRate = 1000)
	public void m() {
	    log.debug("Stuff");
	}

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}

}

@RestController
class R1 {
	@GetMapping
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
