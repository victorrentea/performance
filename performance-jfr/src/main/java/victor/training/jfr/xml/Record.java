package victor.training.jfr.xml;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class Record {
   private String a;
   private String b;
   private int value;

   public String getA() {
      return a;
   }

   public Record setA(String a) {
      this.a = a;
      return this;
   }

   public String getB() {
      return b;
   }

   public Record setB(String b) {
      this.b = b;
      return this;
   }

   public int getValue() {
      return value;
   }

   public Record setValue(int value) {
      this.value = value;
      return this;
   }
}
