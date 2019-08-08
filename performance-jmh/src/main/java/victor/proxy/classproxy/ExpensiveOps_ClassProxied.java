package victor.proxy.classproxy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

public class ExpensiveOps_ClassProxied {
	public Boolean isOdd(int n) { 
		return n % 2 == 0;
	}
}
