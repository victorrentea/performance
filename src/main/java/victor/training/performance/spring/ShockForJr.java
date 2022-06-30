package victor.training.performance.spring;

import org.jetbrains.annotations.NotNull;

public class ShockForJr {
    public static void main(String[] args) {
        String v = m("1");
        System.out.println(v == "s1");
    }

    @NotNull
    private static String m(String p) {
        String v = "s" + p;
        return v;
    }
}
