package victor.training.performance.batch.partitioning;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.performance.batch.core.StartListener;
import victor.training.performance.batch.core.PersonXml;
import victor.training.performance.batch.core.domain.Person;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
@EntityScan("victor.training.performance.batch.sync.domain")
public class PartitionApp  {
   private final JobBuilderFactory jobBuilder;
   private final StepBuilderFactory stepBuilder;
   private final EntityManagerFactory emf;

   public static void main(String[] args) throws IOException {
      SpringApplication.run(PartitionApp.class, args).close();
   }

   @Bean
   public Step partitionedStep() {
      return stepBuilder.get("partitionedStep")
          .partitioner("workerStep", partitioner())
          .step(exportOnePartition())
          .gridSize(4)
          .taskExecutor(partitionExecutor())
//          .partitionHandler() > can send executions to happen over network to other machines
          .build()
          ;
   }

   @Bean
   public ThreadPoolTaskExecutor partitionExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(4);
      executor.setMaxPoolSize(4);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("partition-");
      executor.initialize();
      return executor;
   }

   @Bean
   public MyPartitioner partitioner() {
      return new MyPartitioner();
   }

   @Bean
   public Step exportOnePartition() {
      return stepBuilder.get("exportPartition")
          .<Person, PersonXml>chunk(100)
          .reader(jpaReader(null))
          .processor(toOutputDto())
          .writer(flatFileWriter(null))
          .build();
   }

   private ItemProcessor<Person, PersonXml> toOutputDto() {
      return person -> {
//         log.info("Out");
         return new PersonXml(person);
      };
   }


   @Bean
   @StepScope
   public JpaPagingItemReader<Person> jpaReader(@Value("#{stepExecutionContext['MOD']}")  Integer partitionIndex) {
      log.info("Creating Reader for MOD==="+partitionIndex);

      JpaPagingItemReader<Person> reader = new JpaPagingItemReader<>();
      reader.setQueryString("SELECT p FROM Person p WHERE p.id % 4 = " + partitionIndex);
      reader.setEntityManagerFactory(emf);
      return reader;
   }

   @SneakyThrows
   @Bean
   @StepScope
   public FlatFileItemWriter<PersonXml> flatFileWriter(@Value("#{stepExecutionContext['MOD']}")  Integer partitionIndex) {
      log.info("Creating Writer for MOD==="+partitionIndex);

      BeanWrapperFieldExtractor<PersonXml> fieldExtractor = new BeanWrapperFieldExtractor<>();
      fieldExtractor.setNames(new String[]{"name", "city"});
      fieldExtractor.afterPropertiesSet();

      DelimitedLineAggregator<PersonXml> lineAggregator = new DelimitedLineAggregator<>();
      lineAggregator.setDelimiter(",");
      lineAggregator.setFieldExtractor(fieldExtractor);

      return new FlatFileItemWriterBuilder<PersonXml>()
          .name("itemWriter")
          .resource(new FileSystemResource("data-output-"+partitionIndex+".txt"))
          .lineAggregator(lineAggregator)
          .build();
   }

   @Bean
   public Job basicJob() {
      return jobBuilder.get("basicJob")
          .listener(persistData())
          .incrementer(new RunIdIncrementer())
          .start(partitionedStep())
          .listener(new StartListener())
          .build();

   }

   @Bean
   public PersistDataBeforeJob persistData() {
      return new PersistDataBeforeJob();
   }
}

