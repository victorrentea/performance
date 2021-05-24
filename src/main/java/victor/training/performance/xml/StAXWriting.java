package victor.training.performance.xml;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StAXWriting {
    public static void main(String[] args) throws XMLStreamException {

        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        XMLStreamWriter writer = factory.createXMLStreamWriter(System.out);

        writer.writeStartDocument();
        writer.writeStartElement("root");
        for (int i = 0; i < 2; i++) {
            writer.writeStartElement("record");
            writer.writeAttribute("id",i+"");
            writer.writeStartElement("a");
            writer.writeCharacters("A"+i);
            writer.writeEndElement();
            writer.writeStartElement("b");
            writer.writeCharacters("B" + i);
            writer.writeEndElement();
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }
}
