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

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestingMockTestSolved {
    public static final String ID = "1";
    @InjectMocks
    Testing testing;
    @Mock
    TestingDependency dependencyMock;

    @Test
    void aNotFound() throws Exception {
        HttpClientErrorException notFound = HttpClientErrorException.create(HttpStatus.NOT_FOUND,
                "Not Found", new HttpHeaders(), null, Charset.defaultCharset());
        when(dependencyMock.apiACall(ID)).thenReturn(failedFuture(notFound));
        when(dependencyMock.apiBCall(ID)).thenReturn(completedFuture(new B("b")));

        AB result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new AB(new A("Not Found"), new B("b")));
    }

    @Test
    void aTimeout() throws Exception {
        when(dependencyMock.apiACall(ID)).thenReturn(supplyAsync(()->new A("never"), delayedExecutor(600, TimeUnit.MILLISECONDS)));
        when(dependencyMock.apiBCall(ID)).thenReturn(completedFuture(new B("b")));

        AB result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new AB(new A("Timeout"), new B("b")));
    }

    @Test
    void aSolo() throws Exception {
        when(dependencyMock.apiACall(ID)).thenReturn(completedFuture(new A("SOLO")));

        AB result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new AB(new A("SOLO"), null));
    }

    @Test
    void aAndB() throws Exception {
        when(dependencyMock.apiACall(ID)).thenReturn(completedFuture(new A("a")));
        when(dependencyMock.apiBCall(ID)).thenReturn(completedFuture(new B("b")));

        AB result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new AB(new A("a"), new B("b")));
    }
}
