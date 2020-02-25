package victor.training.concurrency.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak2")
public class Leak2 {
	static ThreadLocal<RequestContext> threadLocal = new ThreadLocal<>();
	
	@GetMapping
	public String test() {
		RequestContext requestContext = new RequestContext();
		threadLocal.set(requestContext);
		try {
			requestContext.rights = new CachingMethodObject()
					.createRightsCalculator();
		} finally {
			threadLocal.remove();
		}
		return "subtle";
	}
}

class RequestContext {

    public CachingMethodObject.UserRightsCalculator rights;
}