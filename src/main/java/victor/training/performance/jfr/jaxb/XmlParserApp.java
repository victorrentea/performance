package victor.training.performance.jfr.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class XmlParserApp {
   public static void main(String[] args) throws JAXBException, IOException {
      File file = new File("jaxb-parsed.xml");
      XmlGenerator.generate(file, 100);

//      PerformanceUtil.waitForEnter();

      System.out.println("Start parsing...");
      long t0 = System.currentTimeMillis();
      int sum = 0;
      for (int i = 0; i < 1000; i++) {
         sum += sumRecords(file);
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1 - t0) + " ms to get sum=" + sum);
      System.out.println("TODO: run this program with JFR and inspect flame graph");
   }

   private static int sumRecords(File file) throws JAXBException, IOException {
      JAXBContext context = JAXBContext.newInstance(RecordList.class, Record.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      int sum = 0;
      try (FileReader reader = new FileReader(file)) {
         RecordList rez = (RecordList) unmarshaller.unmarshal(reader);
         for (Record record : rez.getRecord()) {
            sum += record.getValue();
         }
      }
      return sum;
   }
}

