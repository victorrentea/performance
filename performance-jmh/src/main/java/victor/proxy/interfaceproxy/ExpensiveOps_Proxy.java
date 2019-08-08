package victor.proxy.interfaceproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

public class ExpensiveOps_Proxy {
	
	public static IExpensiveOps_Proxied proxy(IExpensiveOps_Proxied delegate) {
		InvocationHandler h = new InvocationHandler() {
			private final Map<Integer, Boolean> cache = new HashMap<>();
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return cache.computeIfAbsent((Integer) args[0], delegate::isOdd);
			}
		};
		return (IExpensiveOps_Proxied) Proxy.newProxyInstance(ExpensiveOps_Proxy.class.getClassLoader(), 
				new Class<?>[] {IExpensiveOps_Proxied.class}, h);
	}
}
