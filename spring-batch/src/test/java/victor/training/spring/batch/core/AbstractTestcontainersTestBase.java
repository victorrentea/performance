package victor.training.spring.batch.core;

import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
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

   @Container
   static public ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
//   static public ToxiproxyContainer toxiproxy = new ToxiproxyContainer( // for Mac M1 chips
//           DockerImageName.parse("ghcr.io/shopify/toxiproxy:2.5.0-armv6")
//               .asCompatibleSubstituteFor("shopify/toxiproxy"))
       .withNetworkAliases("toxiproxy")
       .withNetwork(network);


   @SneakyThrows
   @DynamicPropertySource
   public static void registerPgProperties(DynamicPropertyRegistry registry) {
      new JdbcContainerProperties(postgres)
          .withP6SPY() // Careful: logging every sql adds extra latency
          .withNetworkDelay(2, toxiproxy)
          .apply(registry);
   }

}
