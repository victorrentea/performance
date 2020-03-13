package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak2")
public class Leak2 {
	static ThreadLocal<MyAppRequestContext> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		MyAppRequestContext requestContext = new MyAppRequestContext();
		threadLocal.set(requestContext);
		requestContext.rights = new CachingMethodObject()
				.createRightsCalculator();
		return "subtle";
	}
}

class MyAppRequestContext {

    public CachingMethodObject.UserRightsCalculator rights;
}