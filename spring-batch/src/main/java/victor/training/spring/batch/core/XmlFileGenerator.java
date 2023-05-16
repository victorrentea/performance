package victor.training.spring.batch.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class XmlFileGenerator {
    public static void main(String[] args) throws IOException {
        generateFile(10000000);
    }
    public static Set<String> citiesNamesGenerated = new HashSet<>();

    public static File generateFile(int recordCount) throws IOException {
        citiesNamesGenerated.clear();
        long t0 = System.currentTimeMillis();
        Random r = new Random();
        File dataFile = new File("data.xml");
        try (Writer writer = new FileWriter(dataFile)){ // TODO how to optimize this?
            writer.write("<personList>\n");
            for (int i = 0; i < recordCount; i++) {
//                int cityId = 1 + i / 1000;
                int cityId = 1 + r.nextInt(4);

                String cityName = "City " + cityId;
                citiesNamesGenerated.add(cityName);
                writer.write("<person><name>elem"+i+"</name><city>"+cityName+"</city></person>\n");
            }
            writer.write("</personList>");
        }
        long t1 = System.currentTimeMillis();

        System.out.println("Generated "+recordCount+" records ("+dataFile.length()/1024+" kb) in " + (t1-t0) + " ms");
        return dataFile;
    }
}
