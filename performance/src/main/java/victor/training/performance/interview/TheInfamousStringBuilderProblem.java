package victor.training.performance.interview;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TheInfamousStringBuilderProblem {
    public static void main(String[] args) throws IOException {
        List<String> elements = IntStream.range(1, 10).mapToObj(String::valueOf).collect(toList());

        // TODO join elements with "," and print them on the console

        // TODO 1M
        // TODO JFR +=
    }
}
