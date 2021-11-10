//package victor.training.performance.jpa;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
//@Slf4j
//@SpringBootTest
//@Transactional
//@Rollback(false)
//public class Composit {
//   @Test
//   void test() {
//
//   }
//}
//
//
//@Data
//@Entity
//class Contact {
//   @Id
//   @GeneratedValue
//   Long id;
//   String name;
//}
//@Entity
//@Data
//@IdClass(PhonePK.class)
//class Phone {
//   @ManyToOne
//
//   Contact parent;
//}
//
//enum PhoneType {
//   HOME, WORK, MOBILE
//}
//class PhonePK implements Serializable {
//   @Enumerated(EnumType.STRING)
//   private PhoneType type;
//
//}