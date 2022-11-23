package victor.training.performance.interview;

import org.springframework.web.bind.annotation.GetMapping;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class NuAsa {
  @GetMapping("TE_DA_AFARA") // security brici
  public void method() {
    Thread t = new Thread(() -> { // fiecare thread are un STACK = 2MB
      // = un spatiu de mem pe care sta??
      System.out.println("ASTA");
      vreoMetoda(2);
      sleepMillis(1000);
    });
    t.start();
  }

  private void vreoMetoda(int i) {
    int a = i + 1;
    String s = a + "#sieu";
    System.out.println(s);
  }
}
