package victor.training.performance.leak.obj;

import java.io.Serializable;

public class Big20MB implements Serializable {
	public String currentUser;
	public int[] largeArray = new int[5*1024*1024]; // = 4b x 5 x 1MB = 20 MB object is easy to spot in a heapdump (didactic)

	public String getCurrentUser() {
		return currentUser;
	}

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

  @Override
  public String toString() {
    // remove package name
    return super.toString().substring(super.toString().lastIndexOf(".")+1);
  }
}