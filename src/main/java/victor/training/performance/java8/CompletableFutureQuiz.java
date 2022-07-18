package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompletableFutureQuiz {

    public void task1() {
        log.debug("Task1");
    }

    public void runInAPrivateGlobalThreadPool() {
        task1(); //todo execute on a shared private global thread pool
    }
}
