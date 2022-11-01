package victor.training.performance.java8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

public class LoosingChildren {
    public static void main(String[] args) {
        Copil child0 = new Copil("Emma");

        Set<Copil> puiiMei = new HashSet<>();
        puiiMei.add(child0);

        System.out.println(puiiMei.contains(child0));
        // adolescenta
        child0.setName("Emma-Simona");

        System.out.println(puiiMei.contains(child0)); //==
        puiiMei.add(child0);

        System.out.println("cati copii am " + puiiMei.size());
        System.out.println("cati copii  " + puiiMei);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Copil {
    private String name; //
}
