package victor.training.performance.jfr.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "records")
public class RecordList {
   List<Record> record = new ArrayList<>();

   public List<Record> getRecord() {
      return record;
   }

   public RecordList setRecord(List<Record> record) {
      this.record = record;
      return this;
   }
}
