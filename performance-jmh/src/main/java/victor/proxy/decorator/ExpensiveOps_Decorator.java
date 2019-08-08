package victor.proxy.decorator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

public class ExpensiveOps_Decorator implements IExpensiveOps_Decorated {
	private final IExpensiveOps_Decorated delegate;
	
	public ExpensiveOps_Decorator(IExpensiveOps_Decorated delegate) {
		this.delegate = delegate;
	}

	private final Map<Integer, Boolean> cache = new HashMap<>();
	
	public Boolean isOdd(int n) {
		return cache.computeIfAbsent(n, delegate::isOdd);
	}
}
