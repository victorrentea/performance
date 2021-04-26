package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.IntStream;

public class TheInfamousStringBuilderProblem {
    public static void main(String[] args) throws IOException {
        List<String> elements = IntStream.range(1, 300_000).mapToObj(String::valueOf).collect(toList());
//300000 * 5 = 1,5M
        // TODO join elements with "," and print them on the console

        long t0 = System.currentTimeMillis();




        // citesti in chunkuri (batch) cate 500-1000 elemente
        generate(new OutputStreamWriter(System.out), elements);

        long t1 = System.currentTimeMillis();


//        ResultSet rs;
//        while (rs.next()) {
//            rs.getString("COL1")
//            rs.getString("COL1")
//        }


        System.out.println("\n Took: " + (t1-t0));

//        System.out.println(s);

        // TODO 300K
        // TODO JFR +=
    }





//    static String atribut
    private static void generate(Writer writer, List<String> elements) throws IOException {
//        StringBuilder s = new StringBuilder();
        for (String element : elements) {
            writer.write(element);
            // s += element;
//            s.append(element);
        }
//        return s.toString();
    }
}
