package victor.training.performance.jpa;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
class Biography {
    @Id
    @GeneratedValue
    private Long id;

}
@Entity
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int age;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Biography bio;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT_ID")
    private Set<Child> children = new HashSet<>();

//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "PARENT_ID")
//    private Set<Vecin> vecini = new HashSet<>();

    private Parent() {
    }

    public Parent(String name) {
        this.name = name;
    }

    public Biography getBio() {
        return bio;
    }

    public Parent setBio(Biography bio) {
        this.bio = bio;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Parent addChild(Child child) {
        children.add(child);
        return this;
    }

    public Set<Child> getChildren() {
        return children;
    }

    public int getAge() {
        return age;
    }

    public Parent setAge(int age) {
        this.age = age;
        return this;
    }

    public String getName() {
        return name;
    }
}