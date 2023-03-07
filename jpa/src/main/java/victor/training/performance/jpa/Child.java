package victor.training.performance.jpa;

import victor.training.performance.jpa.Parent;

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
    private Parent parent;

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

