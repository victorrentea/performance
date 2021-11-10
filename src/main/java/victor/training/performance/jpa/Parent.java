package victor.training.performance.jpa;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
//@NamedNativeQueries({
//@NamedNativeQuery(name = "arbitrar", query = "select parent0_.name        as name3_2_0_,\n" +
//                                             "       STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names\n" +
//                                             "from parent parent0_\n" +
//                                             "         left outer join child c on parent0_.id = c.parent_id\n" +
//                                             "group by parent0_.name")
//})

public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int age;

    @OneToMany(cascade = CascadeType.ALL)
//        , fetch = FetchType.EAGER) // NICIODATA pt ca oriunde scoti
    // findById sau @Query un Parent di nDB, se intample
    // a) fie un JOIN cu randuri in plus in result fie
    // b) Query-uri in plus dupa copii
     // >>>> CHIAR VREAU MEREU COPIII MEI
            // exceptien 0.1% cand faci: DDD si asta e un Aggregat care are Child private Entities
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