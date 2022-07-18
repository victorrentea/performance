package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableFutureQuiz {

    public void task1() {
        log.debug("Task1");
    }

    public void throttling() { // called over a REST API, possibly by a large no of clients
        task1(); //todo execute task1 on a shared private global thread pool with 1 thread, to avoid overlapping executions
        // if there are any errors in task1, they should be thrown out to your caller.
    }

    public void task2() {
        log.debug("Task2");
    }

    public void parallelize() {
        task1();
        task2();
        // todo execute task1() and task2() in parallel and wait for both to complete
    }

    public CompletableFuture<?> returnAPromise() {
        // todo execute task1() async and return a promise that completes when task1 finishes
        // this method should not block!
        task1();
        return null; // todo
    }

    public CompletableFuture<?> logOnError() {
        // same as above +
        // todo log any error occurred before returning the promise
        task1();
        return null;
    }

    public CompletableFuture<?> chainedSteps() {
        // todo execute task1() async and then task2() async,
        //  and return a promise that completes when task2 finishes
        //  !this method should not block!
        task1();
        task2();
        return null;
    }

    public CompletableFuture<?> combine() {
        // todo execute task1() async and  task2() async in parallel,
        //  and return a promise that completes when both tasks are done
        //  !this method should not block!
        task1();
        task2();
        return null;
    }
}
