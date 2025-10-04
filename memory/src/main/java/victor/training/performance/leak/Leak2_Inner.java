package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CalculatorFactory.Calculator;
import victor.training.performance.leak.obj.Big20MB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak2")
public class Leak2_Inner {
  @GetMapping("inner")
  public String endpoint() {
    Calculator calculator = new CalculatorFactory().create();
    work(calculator);
    return "✔";
  }

  private void work(Calculator calculator) {
    sleepMillis(20_000);
  }

  //<editor-fold desc="Irrelevant: entry points of related leaks">
  @GetMapping("anon")
  public String anon() {
    Stream<String> supplier = new CalculatorFactory().anonymousVsLambdas(List.of("a"));
    sleepMillis(20_000); // some long workflow
    return supplier.collect(Collectors.toList()).toString();
  }

  @GetMapping("map")
  public Map<String, Integer> map() {
    Map<String, Integer> map = new CalculatorFactory().mapInit();
    sleepMillis(20_000); // some long workflow
    return map;
  }
  //</editor-fold>
}

class CalculatorFactory {
  public class Calculator {
    public boolean calculate(String data) {
      System.out.println("Simple Code Code");
      // TODO what connects this Calculator instance with bigMac🍔?
      return true;
    }
  }

  private final Big20MB bigMac = new Big20MB(); // 🍔

  public Calculator create() {
    return new Calculator();
  }

  //<editor-fold desc="Lambdas vs Anonymous implementation">
  public Stream<String> anonymousVsLambdas(List<String> input) {
    return input.stream()
        .filter(new Predicate<String>() {
          @Override
          public boolean test(String s) {
            return !s.isBlank();
          }
        });
//            TODO experiment ->, this::
  }
  //</editor-fold>

  //<editor-fold desc="Map init in Java <= 8">
  public Map<String, Integer> mapInit() {
    return new HashMap<>() {{
      put("one", 1);
      put("two", 2);
    }};
  }
  //</editor-fold>
}

/**
 * ⭐️ KEY POINTS
 * - 👍 Instead of inner classes, prefer nested (static) or in a separate file
 * - 😱 new Class(){} and new Interface(){} reference the instance of the containing class
 * - 👍 Stateless Logic: Don't keep request state in fields of classes holding logic
 */