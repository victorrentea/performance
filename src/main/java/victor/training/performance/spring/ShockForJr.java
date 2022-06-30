package victor.training.performance.spring;

import org.jetbrains.annotations.NotNull;

public class ShockForJr {
    public static void main(String[] args) {
        String v = m("1", 7);
        System.out.println(v == "s1");
    }

    @NotNull
    private static String m(String p, int i) {
        String v = p + "s" + p + i;
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        v += "a";
        return v;
    }
}
