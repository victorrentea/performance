package victor.training.performance.interview;

import lombok.Value;

public class ALocariDeAnABouleaCuImutabile {
  public static void main(String[] args) {


    Imutabile o = new Imutabile("init", "alte");


    for (int i = 0; i < 1000_000; i++) {
      o = o.withDate(o.getDate() + i);
    }

  }
}
@Value
final
class Imutabile {
  private final
  String date;
  private final String maiMulte;

  public Imutabile withDate(String date) {
    return  new Imutabile(date, this.maiMulte);
  }
}