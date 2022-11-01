package victor.training.performance.java8.cf;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.java8.cf.Enrich.*;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static victor.training.performance.java8.cf.Enrich.*;
import static victor.training.performance.java8.cf.TestUtils.delayedAnswer;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
public class EnrichTest {
    @Mock
    Dependency dependency;
    @InjectMocks
    Enrich workshop;
    private static final A a = new A();
    private static final B b = new B();
    private static final C c = new C();

    @Test
    void p01_a_par_b() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b(1)).thenReturn(completedFuture(b));

        assertThat(workshop.p01_a_par_b(1).get()).isEqualTo(new AB(a, b));
    }

    @Test
    void p02_a_then_b1() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));

        assertThat(workshop.p02_a_then_b1(1).get()).isEqualTo(new AB(a, b));
    }
    @Test
    void p03_a_then_b1_par_c1() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));
        when(dependency.c1(a)).thenReturn(completedFuture(c));

        assertThat(workshop.p03_a_then_b1_par_c1(1).get()).isEqualTo(new ABC(a, b,c));
    }
    @Test
    @Timeout(500)
    void p03_a_then_b1_par_c1___runs_in_parallel() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenAnswer(delayedAnswer(300, completedFuture(b)));
        when(dependency.c1(a)).thenAnswer(delayedAnswer(300, completedFuture(c)));

        workshop.p03_a_then_b1_par_c1(1).get();
    }

    @Test
    void p04_a_then_b1_then_c2() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b1(a)).thenReturn(completedFuture(b));
        when(dependency.c2(a, b)).thenReturn(completedFuture(c));

        assertThat(workshop.p04_a_then_b1_then_c2(1).get()).isEqualTo(new ABC(a, b,c));
    }

    @Test
    void p05_a_then_b1_then_c2() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenReturn(completedFuture(a));
        when(dependency.b(1)).thenReturn(completedFuture(b));
        when(dependency.c(1)).thenReturn(completedFuture(c));

        assertThat(workshop.p05_a_then_b1_then_c2(1).get()).isEqualTo(new ABC(a, b,c));
    }

    @Test
    @Timeout(400)
    void p05_a_then_b1_then_c2___runs_in_parallel() throws ExecutionException, InterruptedException {
        when(dependency.a(1)).thenAnswer(delayedAnswer(300, completedFuture(a)));
        when(dependency.b(1)).thenAnswer(delayedAnswer(300, completedFuture(b)));
        when(dependency.c(1)).thenAnswer(delayedAnswer(300, completedFuture(c)));

        workshop.p05_a_then_b1_then_c2(1).get();
    }
}
