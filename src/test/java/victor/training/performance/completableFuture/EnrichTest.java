package victor.training.performance.completableFuture;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
public class EnrichTest {
    @Mock
    Enrich.Dependency dependency;
    @InjectMocks
    Enrich workshop;
    private static final Enrich.A a = new Enrich.A();
    private static final Enrich.B b = new Enrich.B();
    private static final Enrich.C c = new Enrich.C();

    @Test
    void p01_a_par_b() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b(1)).thenReturn(completedFuture(b));

        assertThat(workshop.p01_a_par_b(1).get()).isEqualTo(new Enrich.AB(a, b));
    }

    @Test
    void p02_a_then_b1() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));

        assertThat(workshop.p02_a_then_b1(1).get()).isEqualTo(new Enrich.AB(a, b));
    }
    @Test
    void p03_a_then_b1_par_c1() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));
        when(dependency.c1(a)).thenReturn(completedFuture(c));

        assertThat(workshop.p03_a_then_b1_par_c1(1).get()).isEqualTo(new Enrich.ABC(a, b,c));
    }
    @Test
    @Timeout(500)
    void p03_a_then_b1_par_c1___runs_in_parallel() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenAnswer(TestUtils.delayedAnswer(300, completedFuture(b)));
        when(dependency.c1(a)).thenAnswer(TestUtils.delayedAnswer(300, completedFuture(c)));

        workshop.p03_a_then_b1_par_c1(1).get();
    }

    @Test
    void p04_a_then_b1_then_c2() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));
        when(dependency.c2(a, b)).thenReturn(completedFuture(c));

        assertThat(workshop.p04_a_then_b1_then_c2(1).get()).isEqualTo(new Enrich.ABC(a, b,c));
    }

    @Test
    void p05_a_then_b1_then_c2() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b(1)).thenReturn(completedFuture(b));
        when(dependency.c(1)).thenReturn(completedFuture(c));

        assertThat(workshop.p05_a_then_b1_then_c2(1).get()).isEqualTo(new Enrich.ABC(a, b,c));
    }

    @Test
    @Timeout(400)
    void p05_a_then_b1_then_c2___runs_in_parallel() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenAnswer(TestUtils.delayedAnswer(300, completedFuture(a)));
        when(dependency.b(1)).thenAnswer(TestUtils.delayedAnswer(300, completedFuture(b)));
        when(dependency.c(1)).thenAnswer(TestUtils.delayedAnswer(300, completedFuture(c)));

        workshop.p05_a_then_b1_then_c2(1).get();
    }
}
