package victor.training.performance.leak.obj;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Big100MB implements Serializable {
	public int[] largeArray = new int[25 * 1024 * 1024]; // x 4 bytes/int
	public String a = "a data";
	public String b = "b data";
}
