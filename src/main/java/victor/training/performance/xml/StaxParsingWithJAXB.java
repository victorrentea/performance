package victor.training.performance.xml;

import lombok.Data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.io.StringReader;

public class StaxParsingWithJAXB {
    @Data
    @XmlRootElement(name = "record")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RecordXml {
        @XmlAttribute
        private Long id;
        @XmlElement
        private String a;
        @XmlElement
        private String b;
    }
    public static void main(String[] args) throws XMLStreamException, JAXBException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Reader reader = new StringReader("<root><record id=\"1\"><a>A</a><b>B</b></record><record id=\"2\"><a>A2</a><b>B2</b></record></root>");
        XMLEventReader xmlReader = factory.createXMLEventReader(reader);

        JAXBContext jc = JAXBContext.newInstance(RecordXml.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        xmlReader.nextEvent(); // document start event <root>
        System.out.println("START " + xmlReader.nextEvent().asStartElement().getName());
        while (xmlReader.hasNext()) {
            if(xmlReader.peek().isStartElement() &&
               xmlReader.peek().asStartElement().getName().getLocalPart().equals("record")) {

                RecordXml record = (RecordXml) unmarshaller.unmarshal(xmlReader);
                // pun in DB...
                System.out.println("Read: " + record);
            } else {
                XMLEvent e = xmlReader.nextEvent();
                System.out.println("Found unexpected element: " + e);
            }
        }
        System.out.println("Done");
    }
}
