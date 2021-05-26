package victor.training.jpa.perf;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "id_doc_seq")
public class IDDocument {

    @GeneratedValue(generator = "id_doc_seq")
    @Id
    private Long id;
}
