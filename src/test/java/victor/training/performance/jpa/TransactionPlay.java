package victor.training.performance.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.IOException;

@SpringBootTest
public class TransactionPlay {
   @Autowired
   TransactionPlayground playground;

   @Test
   void test() throws IOException { // netranzactiona, ca de ex dintr-un RestController
      System.out.println(playground.getClass());
      System.out.println("------ ");
      playground.tx1(); // HTTP req 1
      System.out.println("------ ");
      playground.tx2(); // HTTP req 2
      System.out.println("------ ");
   }
}

//      new TransactionPlayground() {
//         @Override
//         public void tx1() {
//            if (propagation=REQUIRED && n-amTranzactieDeschisaDeja) startTx;
//            try {
//               super.tx1();
//               commitTx();
//            } catch (RuntimeException e) {
//               rollbackTx();
//            } catch (Exception e) {
//               commitTx(); // WHY THE HACK ? pt ca Spring e batran. si in tineretea lui a pacatuit
// cand aaparut toti devii din BE java erau pe EJB 2. Din care au replicat comportamentul lui @TransactionAttribute
//            }
//         }
//      }
//   }

@Service
@Transactional // default (propagation = Propagation.REQUIRED)
   // face un proxy in jurul TransactionPlayground
class TransactionPlayground {
   @Autowired
   MessageRepo messageRepo;
   @Autowired
   EntityManager entityManager;

   public void tx1() throws IOException {
      Message m = new Message("Mesaj1");
      messageRepo.save(m);
      messageRepo.save(new Message("Mesaj1"));
      messageRepo.save(new Message("Mesaj1"));
      messageRepo.save(new Message("Mesaj1"));
      System.out.println("dupa save entitatea ta sa capete ID: " + m.getId() );
      // faptul ca INSERTurile le vad in log DUPA linia de println de mai sus, demontreaza ca
      // Hibernate functioneaza ca un WRITE CACHE: cand tu inseri, el :nimic. Doar la commit, face flush()

      // entityManager.flush(); // manual flush : in general anti-pattern in prod. Singurul moment cand e necesar este cand invoci cu PL/SQL

      System.out.println(messageRepo.findAll()); // SELECT m FROM Message m --> SQL
      // 0 rezultate (baza era goala)
      // 4 randuri < CORECT. Ca sa le intoarca tre sa-si faca flush

      System.out.println("END of method");
//      messageRepo.save(new Message(null));

//      throw new RuntimeException("Timeout HTTP");

      throw new IOException("e checked nu runtime"); // free tip: NU FOLOSITI NICIODATE ex checked in Java
   }

   public void tx2() {

   }
}

@Getter
@Setter
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "MessageSeq")
class Message {
   @Id
   @GeneratedValue(generator = "MessageSeq")
   private Long id;
   @Column(nullable = false)
   private String name;

   public Message(String name) {
      this.name = name;
   }
}

interface MessageRepo extends JpaRepository<Message, Long> {

}