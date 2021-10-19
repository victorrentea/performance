package victor.training.performance.leaks;

import java.io.Serializable;
import java.util.Date;

public class BigObject20MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[5*1024*1024];
	public String someString;

	public int lookup(int index) {
		return largeArray[index];
	}

	public String getSomeString() {
		return someString;
	}
}


// int *arr
//int x = *(arr + 2)
//int x = arr[2]