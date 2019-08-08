package victor.proxy.classproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

public class ExpensiveOps_ClassProxy {
	
	public static ExpensiveOps_ClassProxied proxy(ExpensiveOps_ClassProxied delegate) {
		Callback callback = new MethodInterceptor() {
			private final Map<Integer, Boolean> cache = new HashMap<>();
			
			public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3) throws Throwable {
				return cache.computeIfAbsent((Integer) args[0], delegate::isOdd);

			}
		};
		return (ExpensiveOps_ClassProxied) Enhancer.create(ExpensiveOps_ClassProxied.class, callback);
		
	}
}
