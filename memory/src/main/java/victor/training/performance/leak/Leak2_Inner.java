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
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.done;
import static victor.training.performance.util.PerformanceUtil.sleepSeconds;

@RestController
@RequestMapping("leak2")
public class Leak2_Inner {
  @GetMapping("inner")
  public String endpoint() {
    Calculator calculator = new CalculatorFactory().create();
    work(calculator);
    return done(); // TODO extract method
  }

  private void work(Calculator calculator) {
    sleepSeconds(30);
  }

  //<editor-fold desc="Similar entry points /implem /subclass">
  @GetMapping("implem")
  public String implem() {
    Stream<String> supplier = new CalculatorFactory().anonymousVsLambdas(List.of("a"));
    sleepSeconds(30); // some long workflow
    return supplier.toList().toString();
  }

  @GetMapping("subclass")
  public Map<String, Integer> subclass() {
    Map<String, Integer> map = new CalculatorFactory().mapInit();
    sleepSeconds(30); // some long workflow
    return map;
  }
  //</editor-fold>
}

class CalculatorFactory {
  private final Big20MB bigMac = new Big20MB(); // 🍔

  public class Calculator {// TODO what connection to bigMac🍔?
    public int calculate() {
      return 42;
    }
  }

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
    // TODO how about ->, ::
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
 * - 👍 Prefer nested (static) over inner classes. Better: move to a separate .java file
 * - 😱 new Class(){} and new Interface(){} reference the instance of the containing class
 * - 😎 javac ≥21 ftw
 */