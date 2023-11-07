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

    @ManyToOne
    private Parent parent; // keep only this side of the bidirectional link
    // if 1 parent has > 1000 children

    private Child() {
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

