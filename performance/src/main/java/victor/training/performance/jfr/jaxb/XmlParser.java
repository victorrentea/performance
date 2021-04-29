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
      File file = new File("data.txt");
      XmlGenerator.generate(file, 500);

      ConcurrencyUtil.waitForEnter();

      int sum = 0;
      for (int i = 0; i < 1000; i++) {
         sum += sumRecords(file);
      }
      System.out.println(sum);
   }

   private static int sumRecords(File file) throws JAXBException, IOException {
      JAXBContext context = JAXBContext.newInstance(Records.class, Record.class);
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

