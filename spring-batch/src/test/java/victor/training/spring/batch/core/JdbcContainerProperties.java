package victor.training.spring.batch.core;

import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.containers.ToxiproxyContainer.ContainerProxy;

@Slf4j
public class JdbcContainerProperties {
   private final JdbcDatabaseContainer<?> jdbcContainer;
   private boolean p6spy;
   private ContainerProxy toxiproxy;

   public JdbcContainerProperties(JdbcDatabaseContainer<?> jdbcContainer) {
      this.jdbcContainer = jdbcContainer;
   }

   public static String injectP6SPY(String originalJdbcUrl) {
      String remainingUrl = originalJdbcUrl.substring("jdbc:".length());
      String p6spyUrl = "jdbc:p6spy:" + remainingUrl;
      System.out.println("Injected p6spy into jdbc url: " + p6spyUrl);
      return p6spyUrl;
   }

   @SneakyThrows
   public String proxyJdbcUrl(ContainerProxy proxy) {
      String originalJdbcUrl = jdbcContainer.getJdbcUrl();
      System.out.println("Original jdbc url = " + originalJdbcUrl);

      String proxyHost = proxy.getContainerIpAddress() + ":" + proxy.getProxyPort();
      String originalHost = jdbcContainer.getHost() + ":" + jdbcContainer.getFirstMappedPort();

      String proxiedJdbcUrl = originalJdbcUrl.replace(originalHost, proxyHost);
      System.out.println("Proxied jdbc url = " + proxiedJdbcUrl);
      return proxiedJdbcUrl;
   }

   public JdbcContainerProperties withP6SPY() {
      this.p6spy = true;
      return this;
   }

   @SneakyThrows
   public JdbcContainerProperties withNetworkDelay(int networkLatencyMillis, ToxiproxyContainer toxiproxyContainer) {
      log.info("Creating Proxy ...");
      this.toxiproxy =  toxiproxyContainer.getProxy(jdbcContainer, 5432);
      toxiproxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, networkLatencyMillis);
      return this;
   }

   @SneakyThrows
   public void apply(DynamicPropertyRegistry registry) {
      registry.add("spring.datasource.url", this::jdbcUrl);
      registry.add("spring.datasource.username", jdbcContainer::getUsername);
      registry.add("spring.datasource.password", jdbcContainer::getPassword);
      registry.add("spring.datasource.driver-class-name", this::driverClassNameSupplier);
   }

   private String driverClassNameSupplier() {
      if (p6spy) {
         return "com.p6spy.engine.spy.P6SpyDriver";
      } else {
         return jdbcContainer.getDriverClassName();
      }
   }

   public String jdbcUrl() {
      String url = jdbcContainer.getJdbcUrl();
      if (toxiproxy != null) {
         url = proxyJdbcUrl(toxiproxy);
      }
      if (p6spy) {
         url = injectP6SPY(url);
      }
      return url;
   }
}
