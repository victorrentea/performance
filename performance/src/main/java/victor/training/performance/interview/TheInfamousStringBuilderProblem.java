package victor.training.performance.interview;

import lombok.SneakyThrows;
import org.jooq.lambda.Unchecked;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {

    public static final int ONE_MB = 1024 * 1024;

    public static void main(String[] args) throws IOException {
        String a = "a";
        String b = "b";

        String ab = a + b;
        String a1 = "a";
        String ab1 = "ab";
        String ab2 = "a" + "1";

        System.out.println(a == a1);
        System.out.println(ab == ab1);
        System.out.println(ab1 == ab2);


        Stream<LocalDate> stream = Stream.of(LocalDate.now(), LocalDate.now());
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(System.out), ONE_MB)) {
            concatDates(stream, writer);
        }
    }


    // fa-mi o metoda care concateneaza o lista de dates data parametru ca string cu "," intre ele
    @SneakyThrows
    public static void concatDates(Stream<LocalDate> dates, Writer writer) {
        writer.write("header");
        dates.forEach(date -> {
            try {
                writer.write(date.format(DateTimeFormatter.ISO_DATE) + ",");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.write("footer");
    }
    // sexy si nu aloca stringuri in prostie
//        s+= dates.stream().map(date -> date.format(DateTimeFormatter.ISO_DATE)).collect(joining(","));
}
