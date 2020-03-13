package victor.training.performance.pools.exercise;

import victor.training.performance.pools.tasks.CPUTask;
import victor.training.performance.pools.tasks.DegradingTask;
import victor.training.performance.pools.tasks.FragileEndpointTask;
import victor.training.performance.pools.tasks.IOTask;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        List<String> results = new ArrayList<>();
        for (int i = 0; i < 20; i++) { // Reading data is usually fast
            String element = i + "";
            String e1 = Tasks.parse(element); // CPU
            String e2 = Tasks.notify(e1); // fast but fragile
            String e3 = Tasks.insert(e2); // degrading
            String e4 = Tasks.marshall(e3); // CPU
            String e5 = Tasks.linearWs(e4); // constant
            results.add(e5);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Delta = " + (t1-t0));
        System.out.println("Processed " + results.size());
    }

}

class Tasks {

    private static final IOTask constantTask = new IOTask(100);
    static String parse(String element) {
        parseTask.run();
        return element + " parsed";
    }

    private static final FragileEndpointTask notifyTask = new FragileEndpointTask(2,10);
    static String notify(String element) {
        notifyTask.run();
        return element + " notified";
    }

    private static final DegradingTask insertTask = new DegradingTask();
    static String insert(String element) {
        insertTask.run();
        return element + " inserted";
    }

    private static final CPUTask marshallTask = new CPUTask(200);
    static String marshall(String element) {
        marshallTask.run();
        return element + " marshalled";
    }

    private static final CPUTask parseTask = new CPUTask(100);

    static String linearWs(String element) {
        constantTask.run();
        return element + " ws";
    }
}
