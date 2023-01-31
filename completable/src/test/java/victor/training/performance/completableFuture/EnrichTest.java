package victor.training.performance.completableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.completableFuture.CaptureSystemOutput.OutputCapture;
import victor.training.performance.completableFuture.Enrich.A;
import victor.training.performance.completableFuture.Enrich.B;
import victor.training.performance.completableFuture.EnrichSolved;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static victor.training.performance.completableFuture.Enrich.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
public class EnrichTest {
    @Mock
    Dependency dependency;
    @InjectMocks
    EnrichSolved workshop;
    private static final A a = new A("a");
    private static final B b = new B("b");
    private static final C c = new C("c");

    @Test
    void p01_a_par_b() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b(1)).thenReturn(completedFuture(b));

        assertThat(workshop.p01_a_par_b(1).get()).isEqualTo(new AB(a, b));

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b(1); // call ONCE
    }

    @Test
    void p02_a_then_b1() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b1(a)).thenReturn(completedFuture(b));

        assertThat(workshop.p02_a_then_b1(1).get()).isEqualTo(new AB(a, b));

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b1(a); // call ONCE
    }
    @Test
    void p03_a_then_b1_par_c1() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b1(a)).thenReturn(completedFuture(b));
        Mockito.when(dependency.c1(a)).thenReturn(completedFuture(c));

        assertThat(workshop.p03_a_then_b1_par_c1(1).get()).isEqualTo(new ABC(a, b,c));

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b1(a); // call ONCE
        Mockito.verify(dependency).c1(a); // call ONCE
    }
    @Test
    @Timeout(value = 500, unit = MILLISECONDS)
    void p03_a_then_b1_par_c1___runs_in_parallel() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b1(a)).thenAnswer(x -> supplyAsync(() -> b, delayedExecutor(300, MILLISECONDS)));
        Mockito.when(dependency.c1(a)).thenAnswer(x -> supplyAsync(() -> c, delayedExecutor(300, MILLISECONDS)));

        workshop.p03_a_then_b1_par_c1(1).get();

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b1(a); // call ONCE
        Mockito.verify(dependency).c1(a); // call ONCE
    }
    @Test
    void p04_a_b_c() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b(1)).thenReturn(completedFuture(b));
        Mockito.when(dependency.c(1)).thenReturn(completedFuture(c));

        assertThat(workshop.p04_a_b_c(1).get()).isEqualTo(new ABC(a, b,c));

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b(1); // call ONCE
        Mockito.verify(dependency).c(1); // call ONCE
    }

    @Test
    @Timeout(value = 500, unit = MILLISECONDS)
    void p04_a_b_c___runs_in_parallel() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenAnswer(x -> supplyAsync(() -> a, delayedExecutor(300, MILLISECONDS)));
        Mockito.when(dependency.b(1)).thenAnswer(x -> supplyAsync(() -> b, delayedExecutor(300, MILLISECONDS)));
        Mockito.when(dependency.c(1)).thenAnswer(x -> supplyAsync(() -> c, delayedExecutor(300, MILLISECONDS)));

        workshop.p04_a_b_c(1).get();
    }
    @Test
    void p05_a_then_b1_then_c2() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
        Mockito.when(dependency.b1(a)).thenReturn(completedFuture(b));
        Mockito.when(dependency.c2(a, b)).thenReturn(completedFuture(c));

        assertThat(workshop.p05_a_then_b1_then_c2(1).get()).isEqualTo(new ABC(a, b, c));

        Mockito.verify(dependency).a(1); // call ONCE
        Mockito.verify(dependency).b1(a); // call ONCE
        Mockito.verify(dependency).c2(a, b); // call ONCE
    }



    @Nested
    class P06_ComplexFlow {
        @Captor
        ArgumentCaptor<A> captorA;
        @BeforeEach
        final void before() {
            Mockito.when(dependency.a(1)).thenReturn(completedFuture(a));
            Mockito.lenient().when(dependency.b1(a)).thenReturn(completedFuture(b));
            Mockito.lenient().when(dependency.c1(a)).thenReturn(completedFuture(c));
        }
        @Test
        void happy() throws ExecutionException, InterruptedException {
            Mockito.when(dependency.saveA(ArgumentMatchers.any())).thenAnswer(x -> completedFuture(x.getArgument(0)));
            Mockito.when(dependency.auditA(ArgumentMatchers.any(), ArgumentMatchers.eq(a))).thenReturn(completedFuture(null));

            workshop.p06_complexFlow(1).get();

            Mockito.verify(dependency).a(1); // called once
            Mockito.verify(dependency).b1(a); // called once
            Mockito.verify(dependency).c1(a); // called once
            Mockito.verify(dependency).saveA(captorA.capture()); // called once
            A a1 = captorA.getValue();
            assertThat(a1.a).isEqualTo("aBc");
            Mockito.verify(dependency).auditA(a1, a); // called once
        }

        @Test
        @Timeout(value = 400, unit = MILLISECONDS)
        void doesNotWaitForAuditToComplete() throws ExecutionException, InterruptedException {
            Mockito.when(dependency.saveA(ArgumentMatchers.any())).thenAnswer(x -> completedFuture(x.getArgument(0)));
            Mockito.when(dependency.auditA(ArgumentMatchers.any(), ArgumentMatchers.eq(a))).thenReturn(supplyAsync(() -> null, delayedExecutor(500, MILLISECONDS)));

            workshop.p06_complexFlow(1).get();

            Mockito.verify(dependency).saveA(ArgumentMatchers.any());
        }
        @Test
        @CaptureSystemOutput
        void doesNotFail_ifAuditFails_butErrorIsLogged(OutputCapture outputCapture) throws ExecutionException, InterruptedException {
            Mockito.when(dependency.saveA(ArgumentMatchers.any())).thenAnswer(x -> completedFuture(x.getArgument(0)));
            Mockito.when(dependency.auditA(ArgumentMatchers.any(), ArgumentMatchers.eq(a))).thenReturn(failedFuture(new NullPointerException("from test")));

            workshop.p06_complexFlow(1).get();

            Mockito.verify(dependency).saveA(ArgumentMatchers.any());
            Assertions.assertThat(outputCapture.toString()).contains("from test");
        }
        @Test
        void errorInB_failsTheWholeFlow() throws ExecutionException, InterruptedException {
            Mockito.when(dependency.b1(a)).thenReturn(failedFuture(new NullPointerException("from test")));

            assertThatThrownBy(() -> workshop.p06_complexFlow(1).get());

            Mockito.verify(dependency, Mockito.never()).saveA(ArgumentMatchers.any());
            Mockito.verify(dependency, Mockito.never()).auditA(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        void errorInSave_doesNotAudit() throws ExecutionException, InterruptedException {
            Mockito.when(dependency.saveA(ArgumentMatchers.any())).thenReturn(failedFuture(new NullPointerException("from test")));

            assertThatThrownBy(() -> workshop.p06_complexFlow(1).get());

            Mockito.verify(dependency, Mockito.never()).auditA(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        @Timeout(value = 900, unit = MILLISECONDS)
        @Disabled("EXTRA HARD")
        void calls_b_c_inParallel() throws ExecutionException, InterruptedException {
            // when(dependency.b1(a)).thenReturn(supplyAsync(() -> b, delayedExecutor(500, MILLISECONDS))); // WRONG
            // Note: thenAnswer(->) calls the -> only when invoked from tested code
            // ==> start ticking the 500 millis only AT PROD CALL, not earlier

            Mockito.when(dependency.b1(a)).thenAnswer(x->supplyAsync(() -> b, delayedExecutor(500, MILLISECONDS)));
            Mockito.when(dependency.c1(a)).thenAnswer(x->supplyAsync(() -> c, delayedExecutor(500, MILLISECONDS)));
            Mockito.when(dependency.saveA(ArgumentMatchers.any())).thenAnswer(x -> completedFuture(x.getArgument(0)));
            Mockito.when(dependency.auditA(ArgumentMatchers.any(), ArgumentMatchers.eq(a))).thenReturn(completedFuture(null));

            workshop.p06_complexFlow(1).get();
        }

    }
    // parallel fetch ?
}
