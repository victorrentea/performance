package victor.training.performance.batch.sync;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import victor.training.util.JdbcContainerProperties;

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

   @Container
   static public ToxiproxyContainer toxiproxy = new ToxiproxyContainer("shopify/toxiproxy:2.1.0")
       .withNetworkAliases("toxiproxy")
       .withNetwork(network);


   @SneakyThrows
   @DynamicPropertySource
   public static void registerPgProperties(DynamicPropertyRegistry registry) {
      new JdbcContainerProperties(postgres)
//          .withP6SPY()
          .withNetworkDelay(2, toxiproxy)
          .apply(registry);
   }

}
