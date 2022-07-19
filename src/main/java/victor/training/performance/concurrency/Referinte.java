package victor.training.performance.concurrency;

import org.springframework.stereotype.Service;

@Service
public class Referinte {

     ExternalDependencyFake instanceFieldOfASingleton = new ExternalDependencyFake(2); // #3

    {
//        bloc de execut
        ExternalDependencyFake ob2 = new ExternalDependencyFake(3);
        // aici intra GC
    }
    static ExternalDependencyFake attr = new ExternalDependencyFake(2); // #2

    public static void main(String[] args) {
        ExternalDependencyFake d = new ExternalDependencyFake(1); // #1

        System.out.println(d);
        ExternalDependencyFake d2 = d;
        System.out.println(d2);

        Integer x = 10015;

        String s= "";
        for (String arg : args) {
            s += arg;
        }

        // GC intra cand ii tuna lui (vede ca mai e putina memorie dispo)
        // intra fix aici dar nu poate curata
        // 1) ob #1 pt ca sunt var in metode in executie (suspendate) care il refera
        // 2) ob #2 pt ca e static field
        // 3) ob #3 pt ca e camp de instanta intr-un singleton tinut in viata de Spring

        d = null;

        d2 = null;
        // aici instanta poate fi GCata
    }
}
