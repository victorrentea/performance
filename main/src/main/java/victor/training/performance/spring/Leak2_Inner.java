package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.CachingMethodObject.UserRightsCalculator;
import victor.training.performance.util.BigObject20MB;
import victor.training.performance.util.PerformanceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("leak2")
public class Leak2_Inner {

  @GetMapping
  public String home() {
    return "Do you know Java?<br>" +
        "<li><a href='/leak2/inner'>Inner class</a>" +
        "<li><a href='/leak2/anon'>Lambdas vs anonymous class</a>" +
        "<li><a href='/leak2/map'>Map{{</a> ";
  }

  @GetMapping("inner")
  public String puzzle() {
    UserRightsCalculator calculator = new CachingMethodObject().createRightsCalculator();
    bizLogicUsingCalculator(calculator);
    return "Done";
  }

  //<editor-fold desc="Entry points of more similar leaks">
  @GetMapping("anon")
  public String anon() {
    Stream<String> supplier = new CachingMethodObject().anonymousVsLambdas(List.of("a"));
    PerformanceUtil.sleepMillis(20_000); // some long workflow
    return supplier.collect(Collectors.toList()).toString();
  }

  @GetMapping("map")
  public Map<String, Integer> map() {
    Map<String, Integer> map = new CachingMethodObject().mapInit();
    PerformanceUtil.sleepMillis(20_000); // some long workflow
    return map;
  }
  //</editor-fold>

  private void bizLogicUsingCalculator(UserRightsCalculator calculator) {
    if (!calculator.hasRight("launch")) {
      return;
    }
    PerformanceUtil.sleepMillis(20_000); // long flow and/or heavy parallel load
  }
}


class CachingMethodObject {
  private BigObject20MB bigMac = new BigObject20MB();

  public UserRightsCalculator createRightsCalculator() {
    return new UserRightsCalculator();
  }

  //<editor-fold desc="Lambdas vs Anonymous implementation">
  public Stream<String> anonymousVsLambdas(List<String> input) {
    return input.stream()
        .filter(new Predicate<String>() { // could leak memory
          @Override
          public boolean test(String s) {
            return !s.isBlank();
          }
        })
        .filter(s -> !s.isBlank()) // lambda does NOT keep ref to params/ fields
        .filter(s -> !s.isBlank() && bigMac!=null); // clojure of lambda now drags along a ref to the bigMap instace
  }

  // more amazing leaks:

  //<editor-fold desc="Map init in Java <= 8">
  public Map<String, Integer> mapInit() {
//    return new HashMap<>() { // anonymous inner subclass
//      { // instance init block
//        put("one", 1); // lazyness map.put()
//        put("two", 2);
//      }
//    };
    return Map.of("one", 1, "two", 2);
  }
  //</editor-fold>

  public static class UserRightsCalculator { // an instance of this is kept on current thread
    public boolean hasRight(String task) {
      System.out.println("Stupid Code ");
      // what's the connection between this instance and the 'bigMac' field ?
      // 🛑 careful with hidden links
      return true;
    }
  }
  //</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the parent instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state
 */