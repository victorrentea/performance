package victor.training.performance.interview;

import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

interface RecordRepo {
    Iterator<Record> iterateAll();
}

class Record {

}

@XmlType
class RecordXml {
    private String a;

    public RecordXml(Record entity) {
        throw new RuntimeException("Method not implemented");
    }
}

public class JAXBWrite {

    public static void main(String[] args) throws JAXBException, IOException {


        JAXBContext context = JAXBContext.newInstance();
        Marshaller m = context.createMarshaller();
        RecordRepo recordRepo = null;

        FileWriter writer = new FileWriter("out.xml");
        writer.write("<data>\n");
        int total = 0;
        Iterator<Record> it = recordRepo.iterateAll();
        while (it.hasNext()) {
            Record entity = it.next();
            RecordXml recordXml = new RecordXml(entity);
            String xmlString = toXmlString(recordXml, m);

            if (total + xmlString.length() > 500_000_000) {
                writer.write("</data>\n");
                writer.close();
                writer = new FileWriter("out+i.xml");
                writer.write("<data>\n");
                total = 0;
            } else {
                total += xmlString.length();
            }
            writer.write(xmlString);
        }
        writer.write("</data>\n");
        writer.close();

    }

    @SneakyThrows
    private static String toXmlString(RecordXml rx, Marshaller m) {
        StringWriter stringWriter = new StringWriter();
        m.marshal(rx, stringWriter);
        return stringWriter.toString();
    }
}
