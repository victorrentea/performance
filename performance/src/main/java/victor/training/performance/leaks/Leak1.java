package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak1")
public class Leak1 {
	
	static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		BigObject20MB bigData = new BigObject20MB();
		String s = "Just allocated: " + bigData.largeArray.length*4 + " Bytes.";
		s+="Remaining Memory: " + Runtime.getRuntime().freeMemory();
		
		threadLocal.set(bigData);
		try {
			System.out.println("Call business method here");
		} finally {
			threadLocal.remove();
		}
		return s;
	}
}
