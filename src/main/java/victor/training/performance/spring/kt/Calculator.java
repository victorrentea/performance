package victor.training.performance.spring.kt;

import java.util.HashSet;
import java.util.Set;

public class Calculator {
    interface Listener {
        void onResult(int result);
    }
    Set<Listener> listeners = new HashSet<>();
    void addListener(Listener listener) {
        listeners.add(listener);
    }
    void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public int sum(int a, int b) {

        int s = a + b;
        for (Listener listener : listeners) {
            listener.onResult(s);
        }
        return s;
    }
}