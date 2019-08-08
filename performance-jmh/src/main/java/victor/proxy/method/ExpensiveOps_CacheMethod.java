package victor.proxy.method;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
public class ExpensiveOps_CacheMethod {
	private Map<Integer, Boolean> cache = new HashMap<>();
	
	public Boolean isOdd(int n) {
		return cache.computeIfAbsent(n, this::isOdd__);
	}
	private Boolean isOdd__(int n) { 
		return n % 2 == 0;
	}
}
