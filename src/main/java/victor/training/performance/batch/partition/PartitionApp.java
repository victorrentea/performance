package victor.training.performance.batch.partition;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import victor.training.performance.batch.sync.domain.Person;
import victor.training.performance.batch.sync.*;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
@EntityScan("victor.training.performance.batch.sync.domain")
public class PartitionApp {
   private final JobBuilderFactory jobBuilder;
   private final StepBuilderFactory stepBuilder;
   private final EntityManagerFactory emf;

   public static void main(String[] args) throws IOException {
      SpringApplication.run(PartitionApp.class, args).close();
   }

   public Step partitionedStep() {
      return stepBuilder.get("partitionedStep")
          .partitioner(exportPartition())
          .partitioner("workerStep", partitioner())
          .build()
          ;
   }

   @Bean
   public MyPartitioner partitioner() {
      return new MyPartitioner();
   }

   public Step exportPartition() {
      return stepBuilder.get("exportPartition")
          .<Person, PersonXml>chunk(5)
          .reader(jpaReader())
          .processor(toXmlProcessor())
          .writer(flatFileWriter())
          .build();
   }

   private ItemProcessor<Person, PersonXml> toXmlProcessor() {
      return person -> {
         return new PersonXml(person);
      };
   }


   @Bean
   public JpaPagingItemReader<Person> jpaReader() {
      JpaPagingItemReader<Person> reader = new JpaPagingItemReader<>();
      reader.setQueryString("SELECT p FROM Person p");
      reader.setEntityManagerFactory(emf);
      return reader;
   }

   @SneakyThrows
   private ItemWriter<PersonXml> flatFileWriter() {
      BeanWrapperFieldExtractor<PersonXml> fieldExtractor = new BeanWrapperFieldExtractor<>();
      fieldExtractor.setNames(new String[]{"name", "city"});
      fieldExtractor.afterPropertiesSet();

      DelimitedLineAggregator<PersonXml> lineAggregator = new DelimitedLineAggregator<>();
      lineAggregator.setDelimiter(",");
      lineAggregator.setFieldExtractor(fieldExtractor);

      return new FlatFileItemWriterBuilder<PersonXml>()
          .name("itemWriter")
          .resource(new FileSystemResource("data-output.txt"))
          .lineAggregator(lineAggregator)
          .build();
   }

   @Bean
   public Job basicJob() {
      return jobBuilder.get("basicJob")
          .incrementer(new RunIdIncrementer())
          .start(exportPartition())
          .listener(new MyJobListener())
          .build();

   }
}

