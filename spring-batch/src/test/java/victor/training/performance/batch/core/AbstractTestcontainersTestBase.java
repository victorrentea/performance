package victor.training.performance.batch.core;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class AbstractTestcontainersTestBase {

   public static Network network = Network.newNetwork();
   static {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> network.close()));
   }

   @Container
   static public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11")
       .withDatabaseName("prop")
       .withUsername("postgres")
       .withPassword("password")
       .withNetwork(network);

   @SneakyThrows
   @DynamicPropertySource
   public static void registerPgProperties(DynamicPropertyRegistry registry) {
      new JdbcContainerProperties(postgres)
          .withP6SPY() // Careful: logging every sql adds extra latency
//          .withNetworkDelay(2, toxiproxy)
          .apply(registry);
   }

}
