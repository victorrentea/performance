package victor.training.concurrency.db;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StringBuilderPlay {


    public static void main(String[] args) {
        String a = "a";
        String b = "b";
        String c = "c";

        String rez = a+b+c;


        rez += b;
        rez += c;

        System.out.println(rez);


        List<String> stringList = Arrays.asList("a","b","c");
        String rez2 = concat(stringList);
    }


    private static String concat(List<String> stringList) { // < 100-1000
        String rez = "";
        for (String s : stringList) {
            rez += s;
        }
        return rez;
    }

    // 10 MB - FIXED
    private static void concat100_000(Stream<String> stringStream, Writer writer) throws IOException { // < 100-1000
        stringStream.forEach(s -> safeWrite(writer, s));
    }

    private static void safeWrite(Writer writer, String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
