package victor.training.jfr.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XmlParser {
   public static void main(String[] args) throws JAXBException, IOException {
      File file = new File("data.txt");
      XmlGenerator.generate(file,   1_00);

      System.out.println("[ENTER] when ready");
      new Scanner(System.in).nextLine();
      System.out.println("Start");

      int sum = 0;
      for (int i = 0; i < 10000; i++) {
         sum += sumRecords(file);
      }
      System.out.println(sum);
   }


   static JAXBContext context; //40%

   static {
      try {
         context = JAXBContext.newInstance(Records.class, Record.class);
      } catch (JAXBException e) {
         e.printStackTrace();
      }
   }

   private static int sumRecords(File file) throws JAXBException, IOException {
      Unmarshaller unmarshaller = context.createUnmarshaller();
      int sum = 0;
      try (FileReader reader = new FileReader(file)) {
         Records rez = (Records) unmarshaller.unmarshal(reader);
         sum = sumVlaues(sum, rez);
      }
      return sum;
   }

   private static int sumVlaues(int sum, Records rez) {
      for (Record record : rez.getRecord()) {
         sum += record.getValue();
      }
      return sum;
   }
}

