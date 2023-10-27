package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.CalculatorFactory.Calculator;
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
           "<li><a href='/leak2/inner'>Hidden links</a>" +
           "<li><a href='/leak2/anon'>Lambdas vs anonymous class</a>" +
           "<li><a href='/leak2/map'>Map{{</a> ";
  }

  @GetMapping("inner")
  public String puzzle() {
    Calculator calculator = new CalculatorFactory().createRightsCalculator();
    bizLogicUsingCalculator(calculator);
    return "Done";
  }

  //<editor-fold desc="Entry points of more similar leaks">
  @GetMapping("anon")
  public String anon() {
    Stream<String> supplier = new CalculatorFactory().anonymousVsLambdas(List.of("a"));
    PerformanceUtil.sleepMillis(20_000); // some long workflow
    return supplier.collect(Collectors.toList()).toString();
  }

  @GetMapping("map")
  public Map<String, Integer> map() {
    Map<String, Integer> map = new CalculatorFactory().mapInit();
    PerformanceUtil.sleepMillis(20_000); // some long workflow
    return map;
  }
  //</editor-fold>

  private void bizLogicUsingCalculator(Calculator calculator) {
    if (!calculator.calculate("launch")) {
      return;
    }
    PerformanceUtil.sleepMillis(20_000); // long flow and/or heavy parallel load
  }
}


class CalculatorFactory {
  public static class Calculator { // inner (non-static) class inside another one keeps a hidden ref to the outer class instacen
    public boolean calculate(String data) {
      System.out.println("Simple Code Code" );
      // what's the connection between this instance and the 'bigMac' field ?
      // 🛑 careful with hidden links
      return true;
    }
  }
  private BigObject20MB bigMac = new BigObject20MB();

  public Calculator createRightsCalculator() {
    return new Calculator();
  }

  // more amazing leaks:

  //<editor-fold desc="Lambdas vs Anonymous implementation">
  public Stream<String> anonymousVsLambdas(List<String> input) {
    return input.stream()
//            .filter(new Predicate<>() { // anonymous interface implem a la Java 7
//              @Override
//              public boolean test(String s) {
//                return !s.isBlank();
//              }
//            })
//            .filter(s -> !s.isBlank() && bigMac!=null); // leak still here
            .filter(s -> !s.isBlank() && bigMac!=null); // does NOT keep a hard reference to the outer class
    // but only to those things in the method/class that are REFERECNED by the lambda
  }
  //</editor-fold>

  //<editor-fold desc="Map init before Java 11's Map.of(...)">
  public Map<String, Integer> mapInit() {
    return new HashMap<>() {// hash map anonymous subclass
      {  // with an instance init block
      put("one", 1);
      put("two", 2);
    }};
  }

  //</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the parent instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state
 */