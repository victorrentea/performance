package victor.training.spring.batch.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.Person;
import victor.training.spring.batch.core.extra.CaptureStartTimeListener;
import victor.training.spring.batch.core.extra.CountTotalItemsListener;
import victor.training.spring.batch.core.extra.LogProgressListener;
import victor.training.spring.batch.core.extra.LogSqlForFirstChunkListener;
import victor.training.spring.batch.util.PerformanceUtil;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchApp {
  private final JobBuilderFactory jobBuilder;
  private final StepBuilderFactory stepBuilder;

  @Bean
  public Job importJob() {
    return jobBuilder.get("importJob")
        .incrementer(new RunIdIncrementer())
        .start(importPersonData())

        // TODO insert in 2 passes: 1) cities, 2) people
//        .start(importCityData()).next(importPersonData())
        .listener(new CaptureStartTimeListener())
        .build();
  }

  @Bean
  public Step importPersonData() {
    return stepBuilder.get("importPersonData")
        .<PersonXml, Person>chunk(5)

        .reader(xmlReader(null))
        .processor(personProcessor())
        .writer(jpaWriter(null))

        .listener(new LogSqlForFirstChunkListener())
        .listener(progressTrackingChunkListener())
        .listener(countTotalNumberOfRecordsListener())

//        .taskExecutor(batchExecutor()) // TODO insert chunks on multiple threads
        .build();
  }

  @Bean
  @StepScope
  public ItemStreamReader<PersonXml> xmlReader(
      @Value("#{jobParameters['FILE_PATH']}") File inputFile
      // there's a Map in the Spring context as a bean named "jobParameters"
  ) {
    log.info("Reading data from file: {}", inputFile);
    if (!inputFile.exists()) throw new IllegalArgumentException("Not Found: " + inputFile);
    StaxEventItemReader<PersonXml> reader = new StaxEventItemReader<>();
    reader.setResource(new FileSystemResource(inputFile));
    reader.setStrict(true);
    reader.setFragmentRootElementName("person");
    Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
    unmarshaller.setClassesToBeBound(PersonXml.class);
    reader.setUnmarshaller(unmarshaller);

    // parallelization of a chunk insert step requires synchronizing the reader
    // this is useful only when parallelizing the chunks
    SynchronizedItemStreamReader<PersonXml> syncReader = new SynchronizedItemStreamReader<>();
    syncReader.setDelegate(reader);
    return syncReader;
  }

  @Bean
  @StepScope
  public PersonProcessor personProcessor() {
    return new PersonProcessor();
  }

  @Bean
  public <T> JpaItemWriter<T> jpaWriter(EntityManagerFactory emf) {
    JpaItemWriter<T> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(emf);
    return writer;
  }

  @Bean
  @StepScope
  public LogProgressListener progressTrackingChunkListener() {
    return new LogProgressListener();
  }

  @Bean
  @StepScope
  public CountTotalItemsListener countTotalNumberOfRecordsListener() {
    return new CountTotalItemsListener();
  }

  @Bean
  public ThreadPoolTaskExecutor batchExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("batch-");
    executor.initialize();
    return executor;
  }

  @Bean
  public Step importCityData() {
    return stepBuilder.get("importCityData")
        .<PersonXml, City>chunk(1000)
        .reader(xmlReader(null))
        .processor(cityMerger())
        .writer(jpaWriter(null))
        .listener(cityMerger())
        .build();
  }

  @Bean
  public CityProcessor cityMerger() {
    return new CityProcessor();
  }

  public static void main(String[] args) throws IOException {
    XmlFileGenerator.generateFile(10_000);
    int dt = PerformanceUtil.measureCall(() -> SpringApplication.run(BatchApp.class, args).close());
    System.out.println("Batch took " + dt + " ms");
  }
}

