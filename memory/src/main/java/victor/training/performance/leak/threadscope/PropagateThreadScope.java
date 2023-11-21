package victor.training.performance.leak.threadscope;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

/**
 * This utility class propagate the thread-scoped data over executors
 */
@Slf4j
@Component
public class PropagateThreadScope implements TaskDecorator {
	private final MyRequestContext requestContext;

	public PropagateThreadScope(MyRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Runnable decorate(Runnable runnable) {
		log.debug("Decorating from thread with user id = " + requestContext.getCurrentUser());
		// propagates data in a @Scope("thread") bean (Spring magic on top of ThreadLocal)
		String callerUser = requestContext.getCurrentUser();
		String requestId = requestContext.getRequestId(); // runs in the submitter thread
		return () -> {
			// later, in the worker thread, restores the current user.
			requestContext.setRequestId(requestId);
			requestContext.setCurrentUser(callerUser); //set on the async thread (different ) 
			log.debug("Restored user id {} on thread", callerUser);
			try {
				runnable.run();
			} finally {
				requestContext.setCurrentUser(null);
				requestContext.setRequestId(null);
			}
		};
	}
}