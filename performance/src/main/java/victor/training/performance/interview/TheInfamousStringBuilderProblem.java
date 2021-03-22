package victor.training.performance.interview;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {
    public static void main(String[] args) throws IOException {
        List<String> elements = IntStream.range(1, 100).mapToObj(String::valueOf).collect(toList());

        // TODO join elements with "," and print them on the console

        metoda(elements.stream(), System.out);


        // TODO 300K
        // TODO JFR +=
    }

    private static void metoda(Stream<String> data, PrintStream writer) {
        data.forEach(s -> {
                writer.print(s);
                writer.print(",");
            }
        );
        // pipelining: nu acumulezi gramada de date in memorie, ci le versi pe IO:
        // XLSX, PDF, CLOB, SOCKET,
    }
}
