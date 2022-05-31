package victor.training.performance.interview;

public class StringEquals {

    public static void main(String[] args) {
        String s1= "a1";
        Long i1 = 127L;
        Long i2 = 127L;

        System.out.println(i1 == i2);

        String s2 = m(1);

        System.out.println(s1 == s2);
    }

    private static String m(int i) {
        return "a" + i;
    }
}
