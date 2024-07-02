package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CalculatorFactory.Calculator;
import victor.training.performance.leak.obj.BigObject20MB;
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

  //<editor-fold desc="html">
  @GetMapping
  public String home() {
    return "Do you know Java?<br>" +
           "<li><a href='/leak2/inner'>Hidden links</a>" +
           "<li><a href='/leak2/anon'>Lambdas vs anonymous class</a>" +
           "<li><a href='/leak2/map'>Map{{</a> ";
  }
  //</editor-fold>

  @GetMapping("inner")
  public String endpoint() {
    Calculator calculator = new CalculatorFactory().createRightsCalculator();
    bizLogicUsingCalculator(calculator);
    return "Done";
  }

  private void bizLogicUsingCalculator(Calculator calculator) {
    if (!calculator.calculate("launch")) {
      return;
    }
    PerformanceUtil.sleepMillis(20_000); // long flow and/or heavy parallel load
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


}


class CalculatorFactory {
  public static class Calculator { // yeeee - 20MB pe cele 10 min de job.
    public boolean calculate(String data) {
      System.out.println("Simple Code Code ");
      // 🛑 what's connects the Calculator instance with the 'bigMac' field ?
      return true;
    }
  }
  private BigObject20MB bigMac = new BigObject20MB();
  public Calculator createRightsCalculator() {
    return new Calculator();
  }

  // other related leaks:

  //<editor-fold desc="Lambdas vs Anonymous implementation">
  public Stream<String> anonymousVsLambdas(List<String> input) {
    return input.stream()
            .filter(new Predicate<String>() { // PROST = anonymous interface implementation
              @Override
              public boolean test(String s) {
                return !s.isBlank();
              }
            })
            .filter(s -> !s.isBlank()) // conceptual e la fel ca aia de sus, dar NU tine ref la instanta parinte de CalculatorFactory
            .filter(s -> !s.isBlank() && bigMac != null) // tine ref la bigMac pt ca o folosessc
        ;
  }
  //</editor-fold>

  //<editor-fold desc="Map init in Java <= 8">
  public Map<String, Integer> mapInit() {
    // #nostalgia
    // doua {{ inseamna ca e un anonymous subclass cu un bloc de initializare de instanta
//    return new HashMap<>() {{
//      put("one", 1);
//      put("two", 2);
//    }};
    // #asada
    return Map.of("one", 1, "two", 2);
  }
  //</editor-fold>
}

/**
 * KEY POINTS
 * - Anonymous subclasses or implementations keep a reference to the parent instance: use `->` and `Map.of`
 * - Avoid nested classes, or make them 'static'
 * - Avoid keeping heavy state
 */