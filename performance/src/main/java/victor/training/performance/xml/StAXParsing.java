package victor.training.performance.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.io.StringReader;

public class StAXParsing {
    public static void main(String[] args) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Reader reader = new StringReader(
            "<root>" +
            "<record id=\"1\"><a>A</a><b>B</b></record>" +
            "<record id=\"2\"><a>A2</a><b>B2</b><telefon>B</telefon></record>" +
            "</root>");
        XMLEventReader xmlReader = factory.createXMLEventReader(reader);

        xmlReader.nextEvent(); // document start event
        System.out.println("START " + xmlReader.nextEvent().asStartElement().getName());
        while (true) {
            XMLEvent nextEvent = xmlReader.nextEvent();
            if (!nextEvent.isStartElement()) break;
            StartElement startRecord = nextEvent.asStartElement();
            System.out.println("START " + startRecord.getName() + " with id " + startRecord.getAttributeByName(new QName("id")));

            xmlReader.nextEvent(); // <a>
            System.out.println("a=" +xmlReader.nextEvent().asCharacters().getData());
            xmlReader.nextEvent(); // </a>

            xmlReader.nextEvent(); // <b>
            System.out.println("b=" +xmlReader.nextEvent().asCharacters().getData());
            xmlReader.nextEvent(); // </b>

            if (xmlReader.peek().isStartElement()) {
                XMLEvent startTelefonTag = xmlReader.nextEvent();
                System.out.println("START TELEFON: " + startTelefonTag);
                System.out.println("telefon=" + xmlReader.nextEvent().asCharacters().getData());
                xmlReader.nextEvent(); // <telefon>
            }


            xmlReader.nextEvent(); // </record>
        }
        System.out.println("Done");
    }
}
