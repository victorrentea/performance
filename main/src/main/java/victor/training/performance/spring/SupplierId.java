package victor.training.performance.spring;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class SupplierId { // these will compile to long in java 24 ( i hope)
  private final long id;
@Deprecated
  public SupplierId(long id) {
    this.id = id;
  }

  // cache
  private static final WeakHashMap<Long, WeakReference<SupplierId>> canonicInstancesNowImMemory =
          new WeakHashMap<>();
  public static SupplierId of(long val) { // canonic-alizaton of ID (to save memory)
    return canonicInstancesNowImMemory.computeIfAbsent(val, id -> new WeakReference<>(new SupplierId(id)))
            .get();
  }
  public long getId() {
    return id;
  }
}

class SomeCode {
  public static void main(String[] args) {
//    Long l = Long.valueOf(4);
    for (int i = 0; i < 100000; i++) {
      SupplierId supplierId = SupplierId.of(i % 10);

      System.out.println(supplierId);
    }
  }
}
