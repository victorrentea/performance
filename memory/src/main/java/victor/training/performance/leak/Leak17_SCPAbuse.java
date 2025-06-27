package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

// WIP
@Slf4j
@RestController
@RequestMapping("leak18")
public class Leak17_SCPAbuse {
  @GetMapping
  public List<Integer> hotEndpoint(List<String> tokens) throws InterruptedException {
    List<String> canonicalizedTokens = tokens.stream().map(s->canonicalize(s)).toList();
    return canonicalizedTokens.stream().map(s-> System.identityHashCode(s)).toList();
  }

  private String canonicalize(String original) {
    return original.intern(); // bad
//    return canonicStrings.computeIfAbsent(original, unused -> new WeakReference<>(original)).get();
  }

  // if no one references a key, entry is removed
  private static final WeakHashMap<String, WeakReference<String>> canonicStrings = new WeakHashMap<>();

  public static void main(String[] args) {
    String s="a";
    f(s);
  }
  private static void f(String s) {
    System.out.println(s == "a");
  }
}
