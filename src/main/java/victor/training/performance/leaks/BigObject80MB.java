package victor.training.performance.leaks;

import java.io.Serializable;
import java.util.Date;

public class BigObject80MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[20 * 1024 * 1024];
	public String interestingPart = "little data";

	public String getInterestingPart() {
		return interestingPart;
	}
}
