package victor.training.performance.batch.sync;

import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.SneakyThrows;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static victor.training.performance.batch.sync.TestcontainersUtil.injectP6SPY;
import static victor.training.performance.batch.sync.TestcontainersUtil.proxyJdbcUrl;

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
      System.out.println("Define Proxy ...");
      ToxiproxyContainer.ContainerProxy proxy = toxiproxy.getProxy(postgres, 5432);
      proxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 10L);

      registry.add("spring.datasource.url", () -> injectP6SPY(proxyJdbcUrl(postgres, proxy)));
//      registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
      registry.add("spring.datasource.username", postgres::getUsername);
      registry.add("spring.datasource.password", postgres::getPassword);
//      registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
      registry.add("spring.datasource.driver-class-name", ()-> "com.p6spy.engine.spy.P6SpyDriver");
   }

}
