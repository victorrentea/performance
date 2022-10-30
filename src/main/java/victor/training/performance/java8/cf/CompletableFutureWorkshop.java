package victor.training.performance.java8.cf;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureWorkshop {

    /** Launch f() then print its response on System.out, without blocking */
    /** Launch f() a then print its response on System.out, without blocking */
    public void method() {
        //
//        CompletableFuture.supplyAsync(() -> f())
//                completecompletethenAccept()
    }

    /**
     *
     * Combining
     * - .thenApply
     * - then Run
     * - flatmap (call CF with the response of CF)
     * - CF0.fork{CF1,CF2}->join{cfResult} * RACE BUG => Immutables
     * -    diff vs Either
     * - anyOf
     *
     * Exceptions
     * - catch() {log}
     * - catch() {wrap exception in new one}
     * - catch() {default value}
     * - catch() {call another CF}
     * - catch() {retry}
     * - finally {cleanup}: whenComplete...
     *
     * Thread Pools
     * - launch on common
     * - launch on dedicated
     * - fire a CF after a delay
     * - receive a CF, process its response on another Executor
     * - combine 2 CF, change what thread executes the BiFunction
     * - propagate thread locals over a ThreadPoolTaskExecutor
     *
     * Testing
     * - complete with result|exception
     * - delay the complete
     * - mocks returning a CF, then completed.
     * - detect blocking ??
     *
     * Advanced Scenarios
     * - timeout > ex | value
     * - Message Bridge (fire on request MQ, wait on a reply MQ to complete the pending httpRequest
     * - buffer (wait for N, fire all)
     * - cache: get/put full async
     * - rate limiting over WebClient adapter
     */
}
