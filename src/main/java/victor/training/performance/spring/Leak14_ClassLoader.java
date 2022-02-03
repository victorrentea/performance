package victor.training.performance.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@Slf4j
@RestController
@RequestMapping("leak14")
public class Leak14_ClassLoader {

	@GetMapping
	public String root() {

		new Thread(() -> {
			try {
				Class<?> c = Thread.currentThread().getContextClassLoader()
					.loadClass("victor.training.performance.spring.Leak14_Suspect");
				System.out.println("CLass: " + c + " in heap: "+ System.identityHashCode(c));
				Object obj = c
					.newInstance();
				System.out.println("Instantiated : " + obj);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
//			new Suspect().doIt();
			log.debug("Thread ends");
		}).start();

		return "WORK IN PROGRESS";
	}
}

class Leak14_Suspect {
	private BigObject20MB staticField = new BigObject20MB();

	private static ThreadLocal<Leak14_Suspect> threadLocal = new ThreadLocal<>();

	public Leak14_Suspect() {
		threadLocal.set(this);
	}

}

