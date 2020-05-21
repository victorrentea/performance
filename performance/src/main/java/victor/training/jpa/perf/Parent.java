package victor.training.jpa.perf;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = "CNP"), indexes = @Index())
public class Parent {
//    @NaturalId
//    cnp
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent"/*, fetch = FetchType.EAGER*/)
    private Set<Child> children = new HashSet<>();

    @ElementCollection
    private Set<Phone> phones = new HashSet<>();

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
        child.setParent(this);
        return this;
    }
    public Parent addPhone(Phone phone) {
        phones.add(phone);
        return this;
    }

    public Set<Child> getChildren() {
        return children;
    }
}