package victor.training.performance.util;

import java.io.Serializable;
import java.util.Date;

public class BigObject20MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[5*1024*1024]; // x 4 / int = 20 MB
	public String someString;

	public int lookup(int index) {
		return largeArray[index];
	}

	public String getSomeString() {
		return someString;
	}
}