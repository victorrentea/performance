package victor.training.performance.batch.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class XmlFileGenerator {
    public static void main(String[] args) throws IOException {
        generateFile(10000000);
    }

    public static void generateFile(int recordCount) throws IOException {
        long t0 = System.currentTimeMillis();
        File dataFile = new File("data.xml");
        try (Writer writer = new FileWriter(dataFile)){ // TODO how to optimize this?
            writer.write("<personList>\n");
            for (int i = 0; i < recordCount; i++) {
                String cityName = "City " + i / 1000;
                writer.write("<person><name>elem"+i+"</name><city>"+cityName+"</city></person>\n");
            }
            writer.write("</personList>");
        }
        long t1 = System.currentTimeMillis();

        System.out.println("Generated "+recordCount+" records ("+dataFile.length()/1024+" kb) in " + (t1-t0) + " ms");
    }
}
