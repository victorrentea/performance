package victor.training.performance.leaks;

import java.io.Serializable;
import java.util.Date;

public class BigObject20MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[5*1024*1024]; // 5M * sizeof(int 64bit=4byte) = 5 x 4 M = 20MB

	public int lookup(int index) {
		return largeArray[index];
	}
}