package victor.training.performance.leak.obj;

import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

public class BigObject20MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[5*1024*1024]; // = 4b x 5 = 20m
	@Setter
	public String someString;

	public BigObject20MB() {
//		CustomJFREvents
//		log.trace
	}
	public int lookup(int index) {
		return largeArray[index];
	}

	public String getSomeString() {
		return someString;
	}
}