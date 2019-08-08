package victor.proxy.cacheable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
public class ExpensiveOps_Cacheable {
	@Cacheable("odds")
	public Boolean isOdd(int n) { 
		return n % 2 == 0;
	}
}
