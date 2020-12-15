package victor.training.performance.interview;

import org.springframework.data.jpa.repository.JpaRepository;

import static java.util.stream.Collectors.toList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.IntStream;

public class TheInfamousStringBuilderProblem {
    public static void main(String[] args) throws IOException {
        List<String> elements = IntStream.range(1, 10).mapToObj(String::valueOf).collect(toList());

        // TODO join elements with "," and print them on the console

        // TODO 300K
        // TODO JFR +=



        try (Writer writer = new FileWriter("a.txt")) {
            met(elements, writer);
        }
    }
 // > 1K de elemente
    private static void met(Iterable<String> elements, Writer writer) throws IOException {
        for (String element : elements) {
           writer.write(element);
        }
    }
    // < 100 cu conteaza
    private static String met(List<String> elements) {
        StringBuilder s = new StringBuilder();
        for (String element : elements) {
           s.append(element);
        }
        return s.toString();
    }



//    @Transactional(readOnly=true)
//    public void method() {
//
//    }
}


interface Repo extends JpaRepository {
//    Stream<User> findByActiveTrue();
//    Page<User>
}