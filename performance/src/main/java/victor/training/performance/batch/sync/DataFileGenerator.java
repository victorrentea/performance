package victor.training.performance.batch.sync;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DataFileGenerator {
    public static void main(String[] args) throws IOException {
        generateFile(10000000);
    }

    public static void generateFile(int recordCount) throws IOException {
        long t0 = System.currentTimeMillis();
        try (Writer writer = new FileWriter("data.xml")){ // TODO how to optimize this?
            writer.write("<root>\n");
            for (int i = 0; i < recordCount; i++) {
                writer.write("<data><value>elem"+i+"</value></data>\n");
            }
            writer.write("</root>");
        }
        long t1 = System.currentTimeMillis();

        System.out.println("Generating "+recordCount+" records took " + (t1-t0));
    }
}
