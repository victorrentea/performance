//package victor.training.performance.leak;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.WeakHashMap;
//
//import static org.springframework.http.ResponseEntity.ok;
//
//// WIP
//@Slf4j
//@RestController
//@RequestMapping("leak18")
//public class Leak17_SCPAbuse {
//  @GetMapping
//  public ResponseEntity<Void> hotEndpoint(String userData) throws InterruptedException {
////    userData = userData.intern();
//    String finalUserData = userData;
//    userData = canonicStrings.computeIfAbsent(userData, (a, k) -> finalUserData);
//    return ok(null);
//  }
//
//  // daca nimeni altcineva nu refera cheia, GC poate sterge entry
//  private static final WeakHashMap<String, String> canonicStrings = new WeakHashMap<>();
//
//  public static void main(String[] args) {
//    String s="a";
//
//    f();
//  }
//
//  private static void f() {
//    String s = "a";
//  }
//}
