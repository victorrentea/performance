package victor.training.performance.leak.obj;

import java.io.Serializable;

public class Big implements Serializable {
	private final int[] largeArray; // = 4b x 5 x 1MB = 20 MB object is easy to spot in a heapdump (didactic)

  public Big(int bytes) {
    largeArray = new int[bytes/2];
  }

  @Override
  public String toString() {
    // remove package name
    return super.toString().substring(super.toString().lastIndexOf(".")+1);
  }
}