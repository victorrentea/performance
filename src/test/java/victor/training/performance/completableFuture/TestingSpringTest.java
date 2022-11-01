package victor.training.performance.completableFuture;

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
        XY XY = testing.methodToTest("1").get();
        assertThat(XY).isEqualTo(new XY(new X("x"), new Y("y")));
    }
}
