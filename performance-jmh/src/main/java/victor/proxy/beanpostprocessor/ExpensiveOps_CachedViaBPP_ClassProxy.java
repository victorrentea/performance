package victor.proxy.beanpostprocessor;

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

public class ExpensiveOps_CachedViaBPP_ClassProxy {
	
	public static ExpensiveOps_CachedViaBPP proxy(ExpensiveOps_CachedViaBPP delegate) {
		Callback callback = new MethodInterceptor() {
			private final Map<Integer, Boolean> cache = new HashMap<>();
			
			public Object intercept(Object arg0, Method method, Object[] args, MethodProxy arg3) throws Throwable {
				return cache.computeIfAbsent((Integer) args[0], delegate::isOdd);

			}
		};
		return (ExpensiveOps_CachedViaBPP) Enhancer.create(ExpensiveOps_CachedViaBPP.class, callback);
		
	}
}
