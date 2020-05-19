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
        Set<String> result = IntStream.rangeClosed(1, max)
                .mapToObj(i -> "A" + i)
                .collect(toSet());
//        Collections.shuffle(result);
        return result;
    }


    // HashMap .put.get.containsKey = O(1) pe baza elem.hashCode
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
