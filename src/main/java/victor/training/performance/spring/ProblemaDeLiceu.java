package victor.training.performance.spring;

public class ProblemaDeLiceu {
    public static void main(String[] args) {


        f(127,127); // intre -128..127 orice Long is Integer vede compilatorul iti va folosi un set de obiecte
        // Integer/Long cacheuite. Iti aceleasi instante == ca sa economiseasca mem.
        // are deja undeva 256 de Integer si Long pe care ti le da
        // ca a vazut ca f des lucrezi cu numere mici  = Integer Cache
        f(128,128);

        String s = "a" + 1;
        g(s, "a1"); // string common pool
    }

    private static void g(String a, String a1) {
        System.out.println(a == a1);
    }

    private static void f(Integer i, Integer i1) {
        System.out.println(i == i1);
    }
}
