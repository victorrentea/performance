package victor.training.performance.jpa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class ManualPKInsert {

   @Autowired
   ContactRepo contactRepo;
//   @Autowired
//   ContactPhoneRepo contactPhoneRepo;
   @Autowired
   EntityManager entityManager;

   @Test
   void insertManualPK() {
      Contact contact = new Contact();
//      contactRepo.save(contact); // fires an extra SELECT in DB

      entityManager.persist(contact); // raw JPA doesn't
   }
}

@Getter
@Setter
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "ContactSeq")
class Contact {
   @Id
//   @GeneratedValue(generator = "ContactSeq")
   private String id = UUID.randomUUID().toString();
   private String name;
}

//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@IdClass(ContactPhoneId.class)
//class ContactPhone {
//   @Data
//   @NoArgsConstructor
//   @AllArgsConstructor
//   static class ContactPhoneId implements Serializable {
//      private Long contact;
//      private Type type;
//   }
//
//   @Id
//   @Enumerated(EnumType.STRING)
//   private Type type;
//
//   enum Type {
//      WORK, HOME, MOBILE
//   }
//
//   @Id
//   @ManyToOne
//   @MapsId("contact") // from the IdClass mentioned
//   private Contact contact;
//
//   private String value;
//
//   public ContactPhone setContact(Contact contact) {
//      this.contact = contact;
//      return this;
//   }
//}

//interface ContactPhoneRepo extends JpaRepository<ContactPhone, ContactPhoneId> {
//
//}


interface ContactRepo extends JpaRepository<Contact, String> {

}
