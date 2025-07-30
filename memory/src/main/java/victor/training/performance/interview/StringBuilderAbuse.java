package victor.training.performance.interview;

public class StringBuilderAbuse {
  public static void main(String[] args) {
    System.out.println(stringPlusEqual());
    System.out.println(stringBuilderAppend());
  }

  private static String stringPlusEqual() {
    String s =" ";
    s+=1;
    s+=" more ";
    s+=1;
    s+=" more ";
    s+=1;
    s+=" more ";
    s+=" more ";
    return s;
  }

  private static String stringBuilderAppend() {
    StringBuilder sb = new StringBuilder();
    sb.append(" ");
    sb.append(1);
    sb.append(" more ");
    sb.append(1);
    sb.append(" more ");
    sb.append(" more ");
    return sb.toString();
  }
}
