package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak1")
public class Leak1 {
	
	static ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();
//	static ThreadLocal<String> currentUser = new ThreadLocal<>();

	@GetMapping
	public String test() {
		BigObject20MB bigData = new BigObject20MB();
		String s = "Just allocated: " + bigData.largeArray.length*4 + " Bytes.";
		s+="Remaining Memory: " + Runtime.getRuntime().freeMemory();

//		currentUser.set("jdoe");
		threadLocal.set(bigData);
		try {
			businessMethod();
		} finally {
			threadLocal.remove();
		}
		return s;
	}

	static ThreadLocal<String> currentUser = new ThreadLocal<>();


	// Spring foloseste @Scope("request") sau SecurityContextHolder   NU curg intre threaduri MEM leak
	// pentru ca Spring se ocupa ca la finalul executiei HTTP request (Http Request Listener) sa faca el .remove pe acele lucrri interne.


	private void businessMethod() {
		System.out.println("Call business method here");
		// TODO think of throw new RuntimeException();
		repoMetod();
	}

	private void repoMetod() {

		System.out.println("INSERT INTO (...., CREATED_BY, CREATED_DATE) VALUES (......, ? , SYSDATE)");
		System.out.println("setParam(user = " + currentUser.get());
	}
}
