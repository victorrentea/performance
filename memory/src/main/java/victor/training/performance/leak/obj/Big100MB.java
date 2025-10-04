package victor.training.performance.leak.obj;

import java.io.Serializable;
import java.util.Date;

public class Big100MB implements Serializable {
	public Date date = new Date();
	public int[] largeArray = new int[25 * 1024 * 1024]; // x 4 bytes/int
	public String interestingPart = "little data";

	public String getInterestingPart() {
		return interestingPart;
	}
}
