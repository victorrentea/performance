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

@SpringBootTest
public class TransactionPlay {
   @Autowired
   TransactionPlayground playground;

   @Test
   void test() { // netranzactiona, ca de ex dintr-un RestController
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
//            } catch (Exception e) {
//               rollbackTx();
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

   public void tx1() {
      Message m = new Message("Mesaj1");
      messageRepo.save(m);
      messageRepo.save(new Message("Mesaj1"));
      messageRepo.save(new Message("Mesaj1"));
      messageRepo.save(new Message("Mesaj1"));
      System.out.println("dupa save entitatea ta sa capete ID: " + m.getId() );
//      messageRepo.save(new Message(null));
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