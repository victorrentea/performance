package victor.training.performance.concurrency;

import java.util.ArrayList;
import java.util.List;

public final class DisctinctThreadSafeList {
  private final List<Integer> contents = new ArrayList<>();

  public synchronized void add(int e) {
    if (!contents.contains(e)) {
      contents.add(e);
    }
  }

  public synchronized List<Integer> getContents() {
    return new ArrayList<>(contents);
  }
}
