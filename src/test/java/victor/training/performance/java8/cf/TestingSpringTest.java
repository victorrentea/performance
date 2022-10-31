package victor.training.performance.java8.cf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWireMock(port = 9999)
public class TestingSpringTest {
    @Autowired
    private Testing testing;

    @Test
    void integrationTest() throws ExecutionException, InterruptedException {
        AB ab = testing.methodToTest("1").get();
        assertThat(ab).isEqualTo(new AB(new A("aaa"), new B("bbb")));
    }
}
