package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

public class ThreadLocals {
    // SecurityCOntextHolder, @Transactional, JDBC Connection, @Scope(request|session)

    public static void main(String[] args) {
        ThreadLocals threadLocals = new ThreadLocals();
        new Thread(() -> threadLocals.controller("john")).start();
        new Thread(() -> threadLocals.controller("jane")).start();
    }

    static String currentUsername; //

    public void controller(String usernameDinToken) {
        currentUsername=usernameDinToken;
        service();
    }

    private void service() { // nu vreau sa pun param username peste tot.
        PerformanceUtil.sleepq(100);
        repo();
    }

    private void repo() {
        System.out.println("INSERT INTo ... CREATED_BY=? , "  + currentUsername);
    }

}
