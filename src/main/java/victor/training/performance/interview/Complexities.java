package victor.training.performance.interview;

import java.util.*;

public class Complexities {
   public static void main(String[] args) {

      List<String> strings = new ArrayList<>();
      cat_ia(strings);


      Map<ElementCustom, Integer> map = new HashMap<>(); // 10M
      Integer emma = map.get(new ElementCustom("Emma")); // O(1) ct

      Set<ElementCustom> puiiMei = new HashSet<>();
      ElementCustom childOne = new ElementCustom("Emma");

      puiiMei.add(childOne); // complex O(1)

      System.out.println(puiiMei.contains(childOne));
      System.out.println(childOne.hashCode());

//      childOne.setName("Emma-Simona");

      System.out.println(childOne.hashCode());
      System.out.println(puiiMei.contains(childOne));

      puiiMei.add(childOne);

      System.out.println(puiiMei);

   }

   private static void cat_ia(List<String> strings) {
      strings.add("a"); // O(1) tip cat de cat constant indiferent de marimea colectiei
      strings.remove("b"); // O(N)
      strings.remove(0); // O(N) pt ArrayList. La LinkedList e O(1) -> de aia le folosim la cozi. (add la final ,remove de la inceput)
   }
}

class ElementCustom {
   private final String name;

   public ElementCustom(String name) {
      this.name = name;
   }

//   public void setName(String name) {
//      this.name = name;
//   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ElementCustom that = (ElementCustom) o;
      return Objects.equals(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
//      return 1; // toate elem aju in aceeasi galeata ==> hash map = LinkedList .get .put = O(N)
   }
   // daca doua ob sunt eq => hash ==
}