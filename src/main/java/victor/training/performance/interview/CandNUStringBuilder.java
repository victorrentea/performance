package victor.training.performance.interview;

public class CandNUStringBuilder {
   public static void main(String[] args) {


      String s = "ceva header";

      s += args + "delim";
      s+=args;

      s+="footer";


      System.out.println(s);

   }
}
