package victor.training.jpa.perf;

import javax.persistence.*;

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;

    private String name;

    private Child() {
    }

    Child setParent(Parent parent) {
        this.parent = parent;
        return this;
    }

    public Child(String name) {
        this.name = name;
    }
}
