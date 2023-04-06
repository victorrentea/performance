package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static victor.training.performance.ThreadLocalIntro.staticCurrentUser;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ThreadLocalIntro {
    public static void main(String[] args) {
        System.out.println("Here come 2 parallel HTTP requests");
        ThreadLocalIntro app = new ThreadLocalIntro();
        CompletableFuture.runAsync(()->app.httpRequest("alice", "Alice's data"));
        CompletableFuture.runAsync(()->app.httpRequest("bob", "Bob's data"));
        sleepMillis(1000);
    }

    private final AController controller = new AController(new AService(new ARepo()));

    public static ThreadLocal<String> staticCurrentUser = new ThreadLocal<>();
    // TODO ThreadLocal<String>

    // inside Spring, JavaEE,..
    public void httpRequest(String currentUser, String data) {
        log.info("Current user is " + currentUser);
        staticCurrentUser.set(currentUser);
        // TODO pass the current user down to the repo WITHOUT polluting all signatures
        controller.create(data);
    }
}


// ---------- Controller -----------
@RestController
@RequiredArgsConstructor
class AController {
    private final AService aService;
// @GetMapping
    public void create(String data) {
        aService.create(data);
    }
}

// ----------- Service ------------
@Service
@RequiredArgsConstructor
class AService {
    private final ARepo aRepo;
private static final ExecutorCarePropagaThreadLocal
    executor = new ExecutorCarePropagaThreadLocal(Executors.newFixedThreadPool(10));
    public void create(String data) {
        sleepMillis(10); // some delay, to reproduce the race bug
        // threadul kafka listener
        String userulOriginal = staticCurrentUser.get(); // in KL thr
        executor.submit(() -> {
            staticCurrentUser.set(userulOriginal); // in worker thr
            try {
                aRepo.save(data);
            } finally {
                staticCurrentUser.remove();
            }
        });
        executor.submit(() -> aRepo.save(data+"#2"));
    }
}
class ExecutorCarePropagaThreadLocal implements ExecutorService {
        private final ExecutorService delegate;

    ExecutorCarePropagaThreadLocal(ExecutorService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void shutdown() {
delegate.shutdown();
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        // aici esti in threadul celui care cheama .submit si are pe thread local date
        // threadul kafka listener
        String userulOriginal = staticCurrentUser.get();

        Future<T> submit = delegate.submit(() -> {

            staticCurrentUser.set(userulOriginal);
            // aici esti in threadul worker din pool care NU are pe thread local date
            try {
                return task.call();
            } finally {
                staticCurrentUser.remove();
            }
        });
        return submit;
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        return null;
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        return null;
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(tasks);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(tasks, timeout, unit);
    }

    @NotNull
    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(@NotNull Runnable command) {
delegate.execute(command);
    }
}

// ----------- Repository ------------
@Repository
@Slf4j
class ARepo {
    public void save(String data) {
//        SecurityContextHolder.getContext().getAuthentication().getName();
        String currentUser = staticCurrentUser.get();
        log.info("INSERT INTO A(data, created_by) VALUES ({}, {})", data, currentUser);
    }
}
