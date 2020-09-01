package victor.training.performance.leaks;

import org.springframework.scheduling.support.CronTrigger;

import java.io.Serializable;
import java.util.Date;

public class BigObject20MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[5*1024*1024];

	public int lookup(int index) {
		return largeArray[index];
	}
}
