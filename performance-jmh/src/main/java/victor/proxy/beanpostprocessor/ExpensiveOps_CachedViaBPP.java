package victor.proxy.beanpostprocessor;

import org.springframework.stereotype.Service;

@Service
public class ExpensiveOps_CachedViaBPP {
	public Boolean isOdd(int n) { 
		return n % 2 == 0;
	}
}
