package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import javax.servlet.http.HttpServletRequest;

public class ThreadLocals {
    // SecurityCOntextHolder, @Transactional, JDBC Connection, @Scope(request|session)

    public static void main(String[] args) {
        ThreadLocals threadLocals = new ThreadLocals();
        new Thread(() -> threadLocals.controller("john")).start();
        new Thread(() -> threadLocals.controller("jane")).start();
    }

    // usage: iti permite pasarea
    static ThreadLocal<String> currentUsername = new ThreadLocal<>(); // iti da acces la o 'copie specifica threadului tau'

    public void controller(String usernameDinToken) {
        currentUsername.set(usernameDinToken);
        service();
    }

    private void service() { // nu vreau sa pun param username peste tot.
        PerformanceUtil.sleepq(100);
        repo();
    }

    private void repo() {
        System.out.println("INSERT INTo ... CREATED_BY=? , "  + currentUsername.get());
    }

}
