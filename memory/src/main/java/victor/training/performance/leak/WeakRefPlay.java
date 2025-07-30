package victor.training.performance.leak;

import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class WeakRefPlay {

  static Map<String, String> map = new HashMap<>();
  public static void main(String[] args) throws IOException {
    File file = new File("data.txt");
    file.delete();
    try (FileWriter fileWriter = new FileWriter(file)) {
      for (int i = 0; i < 100_000; i++) {
        fileWriter.write("key%d %s\n".formatted(i,"x".repeat(1000)+ i%1000));
      }
    }

    Stream<String> stream = Files.lines(file.toPath());
    stream.forEach(line -> {
      String key = line.split(" ")[0];
      String value = line.split(" ")[1];
//      String value = line.split(" ")[1].intern(); // works but dangerous, because you can't take it back from String Common Pool (SCP)
      value = canonicalize(value);
      map.put(key, value);
    });
    System.out.println(PerformanceUtil.getUsedHeap());
    map =null;
    System.out.println(PerformanceUtil.getUsedHeap()); //#2 prints -1 MB because the weak hash map was auto-cleared
    System.out.println(existingInstancesInMem.size());
    existingInstancesInMem=null;
    System.out.println(PerformanceUtil.getUsedHeap());
    // problem: at the same point in time I have in heap identical immutable objects
    // #1 string common pool (risky)
    // #2 Canonicalize objects
  }
//  SoftReference

  // should return the same == instance of another string in memory
//  static HashMap<String, String> existingInstancesInMem = new HashMap<>();

  //removes the entry for a key that is not referenced anywhere
  static WeakHashMap<String, WeakReference<String>> existingInstancesInMem = new WeakHashMap<>();
  private static String canonicalize(String s) {
    if (existingInstancesInMem.containsKey(s)) {
      return existingInstancesInMem.get(s).get();
    }
    existingInstancesInMem.put(s, new WeakReference<>(s));
    return s;
  }
}
