package victor.perf;

public class StringBuilderPlay {
    public String nums(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s+=" " + i;
        }
        return s.substring(1);
    }

    public String numsBuf(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append(" ").append(i);
        }
        return s.substring(1);
    }


}
