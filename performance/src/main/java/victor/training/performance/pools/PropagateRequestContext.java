package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PropagateRequestContext implements TaskDecorator {
	private final MyRequestContext requestContext;

	public PropagateRequestContext(MyRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Runnable decorate(Runnable runnable) {
		log.debug("Decorating from thread with user id = " + requestContext.getCurrentUser());
		String callerUser = requestContext.getCurrentUser();
		String requestId = requestContext.getRequestId();
		return () -> {
			requestContext.setRequestId(requestId);
			requestContext.setCurrentUser(callerUser); //set on the async thread (different ) 
			log.debug("Restored user id {} on thread", callerUser);
			runnable.run();
		};
	}
}