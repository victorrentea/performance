package victor.training.performance.interview;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Hashing {
    private String name;
    private LocalDate creationDate = LocalDate.now();

    public Hashing(String name, LocalDate creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public static void main(String[] args) {
        Collection<?> targetIds = generate(20_000);
        Collection<?> allIds = generate(20_000);

        match(targetIds, allIds);
    }

    private static Collection<?> generate(int max) {
        System.out.printf("Generating shuffled sequence of %,d elements...%n", max);
        Set<Elem> result = IntStream.rangeClosed(1, max)
                .mapToObj(i -> "A" + i)
                .map(Elem::new)
                .collect(toSet());
//        Collections.shuffle(result);
        return result;
    }

    static class Elem {
        String s;

        public Elem(String s) {
            this.s = s;
        }
//        public int hashCode() {
//            return 0;
//            //degenerat: pierzi tot castigul de O(1) de prin colectiile Hash*
//        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Elem elem = (Elem) o;
            return Objects.equals(s, elem.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }


//        A) 1.h=2.h
//        B) 1.e(2)
        // B ==> A .. A nu implica B.
        // Adica pot exita 2 elem cu hash egal dar equals false

    }

    // HashMap .put.get.containsKey = O(1) pe baza elem.hashCode
    // LinkedHashMap/Set - pastreaza si ordinea inserarii cheilor. adica la iterare le e aceeasi ordine
    // HashSet<> .add.remove.contains = O(1) pe baza unui HashMap
    // TreeSet<> .add.remote.contains = O(lgN) pe baza a TreeMap cu Comparable<>
    // ArrayList<> .add.remove.contains = O(N)

    // O(N^2) sau ..  mai rapid
    private static void match(Collection<?> targetIds, Collection<?> allIds) {
        System.out.println("Matching...");
        long t0 = System.currentTimeMillis();
        int n = 0;
        for (Object a : targetIds) { // x N
            if (allIds.contains(a)) { // O(N) sau O(logN) sau O(1) in fct de tipul allIds.
                n ++;
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Got: " + n);
        System.out.printf("Matching Took = %,d%n", t1 - t0);
    }


}
