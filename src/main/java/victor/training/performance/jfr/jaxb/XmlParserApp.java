package victor.training.performance.jfr.jaxb;

import victor.training.performance.PerformanceUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;

public class XmlParserApp {
   public static void main(String[] args) throws JAXBException, IOException {
//      File file = new File("jaxb-parsed.xml");
//      XmlGenerator.generate(file, 100);

      String xml = "<records>" +
                   "<record><a>a0</a><b>b0</b><value>0</value></record>" +
                   "<record><a>a1</a><b>b1</b><value>1</value></record>" +
                   "</records>";
      PerformanceUtil.waitForEnter();

      System.out.println("Start parsing...");
      long t0 = System.currentTimeMillis();
      int sum = 0;
      for (int i = 0; i < 1000; i++) {
         sum += sumRecords(xml);
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1 - t0) + " ms to get sum=" + sum);
      System.out.println("TODO: run this program with JFR and inspect flame graph");
   }

   private static int sumRecords(String xml) throws JAXBException, IOException {
      JAXBContext context = JAXBContext.newInstance(RecordList.class, Record.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      int sum = 0;
      try (StringReader reader = new StringReader(xml)) {
         RecordList rez = (RecordList) unmarshaller.unmarshal(reader);
         for (Record record : rez.getRecord()) {
            sum += record.getValue();
         }
      }
      return sum;
   }
}

