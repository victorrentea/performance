package victor.training.performance.completableFuture;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled //REMOVE ME AND WRITE THE TESTS
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
        // TODO setup mocks

        XY result =  testing.methodToTest(ID).get();

        assertThat(result).isEqualTo(new XY(new X("Not Found"), new Y("y")));
    }

    @Test
    void aTimeout() throws Exception {
        // TODO: hint: .thenAnswer instead of .thenReturn
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
}
