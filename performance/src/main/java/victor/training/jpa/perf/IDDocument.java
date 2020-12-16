package victor.training.jpa.perf;

import javax.persistence.*;
import java.util.UUID;

@Entity
@SequenceGenerator(name = "MySequenceGenerator")
public class IDDocument {
    //the fastest
//    private String id = UUID.randomUUID().toString();

    // ineficient
//    @GeneratedValue(strategy = GenerationType.IDENTITY)

//    WORST:
//    @GeneratedValue(strategy = GenerationType.TABLE)

    @Id
    @GeneratedValue(generator = "MySequenceGenerator")
    private Long id;

//    public static void main(String[] args) {
//        System.out.println(UUID.randomUUID().toString());
//    }
}
