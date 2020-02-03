package victor.training.concurrency;

import org.jooq.lambda.Unchecked;

import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

public class StringuriCuPlus {
    public static void main(String[] args) {

        String a = "a" + 1;
        String s = a + 2;


    }
    public void m(Stream<Integer> numere, Writer writer) {
        numere.forEach(Unchecked.consumer(i -> writer.write(i + " ")));
    }
}
