package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.CalculatorFactory.Calculator;
import victor.training.performance.leak.obj.Big20MB;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ofPattern;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@RequestMapping("leak2")
public class Leak2_Inner {
  @GetMapping("inner")
  public String endpoint() {
    Calculator calculator = new CalculatorFactory().create();
    work(calculator);
    return "✔ " + LocalTime.now().format(ofPattern("hh:mm:ss")); // TODO extract method
  }

  private void work(Calculator calculator) {
    sleepMillis(20_000);
  }

  //<editor-fold desc="Similar entry points /anon /map">
  @GetMapping("anon")
  public String anon() {
    Stream<String> supplier = new CalculatorFactory().anonymousVsLambdas(List.of("a"));
    sleepMillis(30_000); // some long workflow
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
  private final Big20MB bigMac = new Big20MB(); // 🍔

  public class Calculator {
    public boolean calculate(String data) {
      System.out.println("Simple Code ");
      // TODO what connects this Calculator instance with bigMac🍔?
      return true;
    }
  }

  public Calculator create() {
    return new Calculator();
  }

  //<editor-fold desc="Map init in Java <= 8">
  public Map<String, Integer> mapInit() {
    return new HashMap<>() {{
      put("one", 1);
      put("two", 2);
    }};
  }
  //</editor-fold>

  //<editor-fold desc="Lambdas vs Anonymous implementation">
  public Stream<String> anonymousVsLambdas(List<String> input) {
    return input.stream()
        .filter(new Predicate<String>() {
          @Override
          public boolean test(String s) {
            return !s.isBlank();
          }
        });
    // TODO how about ->, this::
  }
  //</editor-fold>
}

/**
 * ⭐️ KEY POINTS
 * - 👍 Instead of inner classes, prefer nested (static) or in a separate file
 * - 😱 new Class(){} and new Interface(){} reference the instance of the containing class
 * - 👍 Stateless Logic: Don't keep request state in fields of classes holding logic
 */