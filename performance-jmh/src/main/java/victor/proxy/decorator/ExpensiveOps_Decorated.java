package victor.proxy.decorator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
public class ExpensiveOps_Decorated implements IExpensiveOps_Decorated{
	public Boolean isOdd(int n) { 
		return n % 2 == 0;
	}
}
