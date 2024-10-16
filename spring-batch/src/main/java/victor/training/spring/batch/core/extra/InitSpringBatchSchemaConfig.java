package victor.training.spring.batch.core.extra;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(BatchProperties.class)
// because https://stackoverflow.com/questions/75615067/spring-batch-test-failing-with-error-table-batch-job-instance-not-found
public class InitSpringBatchSchemaConfig {
  @Bean
  @ConditionalOnMissingBean(BatchDataSourceScriptDatabaseInitializer.class)
  BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(
      DataSource dataSource,
      @BatchDataSource ObjectProvider<DataSource> batchDataSource,
      BatchProperties properties) {
    return new BatchDataSourceScriptDatabaseInitializer(
        batchDataSource.getIfAvailable(() -> dataSource),
        properties.getJdbc());
  }

}
