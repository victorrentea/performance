package victor.training.performance.completableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
        HttpClientErrorException notFoundException = HttpClientErrorException.create(HttpStatus.NOT_FOUND,
                "Not Found", new HttpHeaders(), null, Charset.defaultCharset());
        when(dependencyMock.fetchX(ID)).thenReturn(failedFuture(notFoundException));
        when(dependencyMock.fetchY(ID)).thenReturn(completedFuture(new Y("y")));

        XY result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("Not Found"), new Y("y")));
    }

    @Test
    void aTimeout() throws Exception {
        when(dependencyMock.fetchX(ID)).thenReturn(supplyAsync(()->new X("never"), delayedExecutor(600, MILLISECONDS)));
        when(dependencyMock.fetchY(ID)).thenReturn(completedFuture(new Y("y")));

        XY result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("Timeout"), new Y("y")));
    }

    @Test
    void aSolo() throws Exception {
        when(dependencyMock.fetchX(ID)).thenReturn(completedFuture(new X("SOLO")));

        XY result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("SOLO"), null));
    }

    @Test
    void aAndB() throws Exception {
        when(dependencyMock.fetchX(ID)).thenReturn(completedFuture(new X("x")));
        when(dependencyMock.fetchY(ID)).thenReturn(completedFuture(new Y("y")));

        XY result = testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("x"), new Y("y")));
    }
}
