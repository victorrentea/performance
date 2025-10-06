package victor.training.performance.leak.obj;

import java.io.Serializable;

public class Big implements Serializable {
	private final int[] largeArray; // = 4b x 5 x 1MB = 20 MB object is easy to spot in a heapdump (didactic)

  public Big(int bytes) {
    largeArray = new int[bytes/4];
  }

  @Override
  public String toString() {
    return "Big("+(largeArray.length * 4) +" bytes)";
  }
}