package victor.training.performance.spring;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;

public class CumNeBatemJocDeHashMapo {
    public static void main(String[] args) {

        Map<Copil, Integer> mapaCareSeMiscaCaUnArray = new HashMap<>();

        Set<Copil> puiiMei = new HashSet<>();

        Copil childPoc = new Copil("Emma");
        puiiMei.add(childPoc);

        System.out.println(puiiMei.contains(childPoc));
        System.out.println(childPoc.hashCode());
        System.out.println("Adolescenta");

        childPoc.setName("Emma-Simona");
        System.out.println(childPoc.hashCode());
        System.out.println(puiiMei.contains(childPoc));

        puiiMei.add(childPoc);
        System.out.println(puiiMei.size());

    }
}

@Entity
//@Data // genereaza hashcode equals pe TOATE campurile, inclusiv ID.
    // NU PUNE @Data pe @ENtity, ci doar @Getter @Setter.
class Copil {
    @Id
    @GeneratedValue // tu il lasi null, iar Hibernate la .save() ii seteaza campul.
    private Long id;
    private String name;// campurile incluse in equals/hashcode tre sa fie finale.
    public Copil(String name) {this.name=name;}

    public void setName(String name) {
        this.name = name;
    }
// hashCode / equals contract:
    // - REGULA: daca sunt equals , tre sa aiba hashCode egal !!!
    // - dar daca au hash=, nu neaparat sunt equals.
    // best practice: aceasi elemente care intra in calculul equals sa intre si in hashCode


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Copil key = (Copil) o;
        return Objects.equals(name, key.name);
    }

    @Override
    public int hashCode() {
//        return 1;// absolute evil
//        return name.length(); // prea slab.
//        return name.hashCode();
//        return Objects.hashCode(name, gender);
        return name.hashCode();
    }
}