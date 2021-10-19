package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.jfr.jaxb.Record;
import victor.training.performance.jfr.jaxb.RecordList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Slf4j
@RestController
@RequestMapping("profile/jaxb")
@RequiredArgsConstructor
public class Profile6_JAXB {

   static JAXBContext context;

   static {
      try {
         context = JAXBContext.newInstance(RecordList.class, Record.class);
      } catch (JAXBException e) {
         throw new RuntimeException(e);
      }
   }

   @GetMapping
   public int sumRecords(@RequestParam String xml) throws JAXBException {
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
