package victor.training.concurrency.leaks;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@SuppressWarnings("serial")
@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContext implements Serializable {

	private Map<String, BigObject20MB> cache = new HashMap<>();
	
	public BigObject20MB tryCache(String key, Callable<BigObject20MB> loadMethod) throws Exception {
		if (cache.containsKey(key)) {
			return cache.get(key); // cache hit
		}
		BigObject20MB newObject = loadMethod.call();
		cache.put(key, newObject); // NU pe sesiune!!
		return newObject;
	}
}
