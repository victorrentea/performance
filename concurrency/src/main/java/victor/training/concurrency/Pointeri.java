package victor.training.concurrency;

public class Pointeri {
    public static void main(String[] args) {

        String s = "aasdsadasdsa";
        String s1 = s + 1;

        Pointeri p = new Pointeri();
        System.out.println(p.toString());

        // TODO

        new A();

//        new int[100]
    }
}


class A {
    private B b = new B();
    private B b1 = new B();
    private B b2 = new B();
    private B b3 = new B();
}
class B {

}
