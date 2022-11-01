package victor.training.performance.java8.cf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestingMockTest {
    public static final String ID = "1";

    @InjectMocks
    Testing testing;
    @Mock
    TestingDependency dependencyMock;

    @Test
    void aNotFound() throws Exception {
        HttpClientErrorException notFound = HttpClientErrorException.create(HttpStatus.NOT_FOUND,
                "Not Found", new HttpHeaders(), null, Charset.defaultCharset());
//         when(dependencyMock.apiACall(1)).thenThrow(new Exception); // NU!  o metoda ce intoarce CF NU ARE voie sa throw
//         when(dependencyMock.apiACall(1)).thenReturn(failed....);
        // va mai trebui sa mockuiti si apiBCall()

        XY result =  testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("Not Found"), new Y("b")));
    }

    @Test
    void aTimeout() throws Exception {
        // TODO: hint: .thenAnswer instead of .thenReturn
        when(dependencyMock.apiACall(ID)).thenAnswer(x -> {
//            Thread.sleep(1000); // NU: face ca CF sa fie returnat cu intarziere.

            // tu vrei sa intorci instant un CF care sa complteze cu intarziere.
            return CompletableFuture.supplyAsync(() -> new X("s"),
                    CompletableFuture.delayedExecutor(1000, MILLISECONDS));
            // acest CF va termina in 1000 ms
        });
        fail("TODO");
    }

    @Test
    void aSolo() throws Exception {
        fail("TODO");
    }

    @Test
    void aAndB() throws Exception {
        fail("TODO");
    }
    // DACA AI TERMINAT, fa ThreadPoolTest sa treaca acu!
}
