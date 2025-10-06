package victor.training.performance.leak;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("profile/jaxb")
@RequiredArgsConstructor
public class Profile6_JAXB {

   // TODO: run this program with JFR enabled and inspect flame graph
   //  (easiest is to connect to the process from Java Mission Control)

   @GetMapping
   public int sumRecords(@RequestParam String xml) throws JAXBException {
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

   @XmlType
   @Data
   public static class Record {
      private String a;
      private String b;
      private int value;
   }

   @Data
   @XmlRootElement(name = "records")
   public static class RecordList {
      List<Record> record = new ArrayList<>();
   }

}




