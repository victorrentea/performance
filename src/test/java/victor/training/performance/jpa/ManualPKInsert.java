package victor.training.performance.jpa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.ContactPhone.ContactPhoneId;
import victor.training.performance.jpa.ContactPhone.Type;

import javax.persistence.*;
import java.io.Serializable;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class ManualPKInsert {

   @Autowired
   ContactRepo contactRepo;
   @Autowired
   ContactPhoneRepo contactPhoneRepo;
   @Autowired
   EntityManager entityManager;

   @Test
   void insertManualPK() {
      Contact contact = new Contact();
      contact.setId(15L);
      contactRepo.save(contact); // fires an extra SELECT in DB
//      entityManager.persist(contact); // raw JPA doesn't

      // Composite PK
      ContactPhone phone = new ContactPhone()
//          .setId(new ContactPhoneId(contact.getId(), Type.WORK))
          .setType(Type.WORK)
          .setContact(contact)
         .setValue("999");
//      phone.getId().setType(Type.WORK);
      contactPhoneRepo.save(phone);

      System.out.println(contactPhoneRepo.findById(new ContactPhoneId(contact.getId(), Type.WORK)));
   }
}

@Getter
@Setter
@NoArgsConstructor
@Entity
class Contact {
   @Id
   private Long id;
   private String name;
}

@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(ContactPhoneId.class)
class ContactPhone {
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   static class ContactPhoneId implements Serializable {
      private Long contact;
      private Type type;
   }

   @Id
   @Enumerated(EnumType.STRING)
   private Type type;

   enum Type {
      WORK, HOME, MOBILE
   }

   @Id
   @ManyToOne
   @MapsId("contact") // from the IdClass mentioned
   private Contact contact;

   private String value;

   public ContactPhone setContact(Contact contact) {
      this.contact = contact;
      return this;
   }
}

interface ContactPhoneRepo extends JpaRepository<ContactPhone, ContactPhoneId> {

}


interface ContactRepo extends JpaRepository<Contact, Long> {

}
