package victor.training.performance.interview;

import java.util.*;

public class SumString {

   public static void main(String[] args) {
      String a = "a";
      int i = 1;
      StringBuilder a1 = new StringBuilder(a + i);

      for (int j = 0; j < 100; j++) {
         a1.append(j);
      }
      System.out.println(a1);
      List<Integer> list = new ArrayList<>();
      list.add(1); // complexitatea amortizata este = O(1)
      list.add(0, 1); // O(N) - e scumpa frate pt ArrayList, O(1) pt LinkedList
      // folositi Linked List doar pentru cozi

//      Map<Copil, String> map = new HashMap<>();
//      map.put(new Copil(), "1");

      Set<Copil> puiiMei = new HashSet<>();
      Copil emma = new Copil("Emma");
      puiiMei.add(emma);

      System.out.println(puiiMei.contains(emma));
      System.out.println(emma.hashCode());

      emma.setName("Emma-Simona");

      System.out.println(emma.hashCode());
      System.out.println(puiiMei.contains(emma));

      // nu implementa hashCode/equals pe @Entity incluzand id-ul in calcul (ca-l schimba hibernate)!!!

//      List<String> list2 = new LinkedList<>();
//      list2.add(null);
   }
}

class Copil {
   private String name;

   public Copil(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Copil copil = (Copil) o;
      return Objects.equals(name, copil.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }
}


// ArrayList
