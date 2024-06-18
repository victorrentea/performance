package victor.training.performance.jpa.parent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne //  <-> bidirectional link  // ewwwww bidirectional e rau
    private Parent parent;

    public Child() {
    }

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}

