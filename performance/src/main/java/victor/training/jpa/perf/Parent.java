package victor.training.jpa.perf;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="PARENTS")
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.PERSIST)
//    @BatchSize(size= 100)
    @JoinColumn(name = "PARENT_ID")
    private Set<Child> children = new HashSet<>();

    private Parent() {
    }

    public Parent(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Parent addChild(Child child) {
        children.add(child);
        return this;
    }

    public Parent setName(String name) {
        this.name = name;
        return this;
    }

    public Set<Child> getChildren() {
        return children;
    }
}