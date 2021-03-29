package victor.training.performance.jfr.jaxb;

import victor.training.performance.ConcurrencyUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class XmlParser {
   public static void main(String[] args) throws JAXBException, IOException {
      File file = new File("data.xml");
//      XmlGenerator.generate(file, 50);

      ConcurrencyUtil.waitForEnter();


      long t0 = System.currentTimeMillis();
      int sum = 0;
      for (int i = 0; i < 1000; i++) {
         sum += sumRecords(file);
      }
      System.out.println(sum);
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1-t0));
   }

   static JAXBContext context = getContext();
   private static JAXBContext getContext() {
      try {
         return JAXBContext.newInstance(Records.class, Record.class);
      } catch (JAXBException e) {
         throw new RuntimeException(e);
      }
   }

   private static int sumRecords(File file) throws JAXBException, IOException {
      Unmarshaller unmarshaller = context.createUnmarshaller();
      int sum = 0;
      try (FileReader reader = new FileReader(file)) {
         Records rez = (Records) unmarshaller.unmarshal(reader);
         for (Record record : rez.getRecord()) {
            sum += record.getValue();
         }
      }
      return sum;
   }
}

