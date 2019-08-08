package victor.training.concurrency.tricks;

public class Pointer {

    public static void main(String[] args) {
        Pointer sameInstance = new Pointer();
        System.out.println(sameInstance);
        // or if overriden hashCode:
        System.out.println(Integer.toHexString(System.identityHashCode(sameInstance)));

    }

//    @Override
//    public String toString() {
//        return "Destept";
//    }
}
