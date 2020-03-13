package victor.training.concurrency.interview;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TheInfamousStringBuilderProblem {
    public static void main(String[] args) throws IOException {
        // TODO explore:
        String a = "a"; // goes to String Pool
        String b = "b";
        if (a == "a") {
            System.out.println("WTH?!");
        }
        String c = a + b + a; //2
        c += a; // one more


        List<String> list = asList("a", "b", "c");

        String s = infamous(list);

        try (Writer w = new FileWriter("out.txt")) {
            w.write(s);
        }
        // TODO more elements ?

        // TODO Where could you offload data? File, CLOB, httpServletResponse.getWriter()

        System.out.println("Done");
    }

    private static String infamous(List<String> list) {
        String s = "Header";
        for (String string : list) {
            s += string + "\n";
        }
        return s;
    }
}
