package victor.training.performance.batch.core;

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
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import victor.training.performance.batch.core.domain.City;
import victor.training.performance.batch.core.domain.Person;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;

import static victor.training.performance.util.PerformanceUtil.measureCall;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchApp {
  private final JobBuilderFactory jobBuilder;
  private final StepBuilderFactory stepBuilder;
  private final EntityManagerFactory emf;

  public static void main(String[] args) throws IOException {
    XmlFileGenerator.generateFile(10_000);
    int dt = measureCall(() -> SpringApplication.run(BatchApp.class, args).close());
    System.out.println("Batch took " + dt + " ms");
  }


  @Bean
  public Job basicJob() {
    return jobBuilder.get("basicJob")
        .incrementer(new RunIdIncrementer())
        .start(importPersonsInChunks())

        // TODO insert in 2 passes: 1) cities, 2) people
        // .start(importCitiesFirstPass()).next(importPersonsInChunks())
        .listener(new StartListener())
        .build();
  }

  @Bean
  public Step importPersonsInChunks() {
    return stepBuilder.get("importPersonsInChunks")
        .<PersonXml, Person>chunk(5)
        .reader(xmlReader(null))
        .processor(personProcessor())
        .writer(jpaWriter())
        // .taskExecutor(batchExecutor()) // process each chunk in a separate thread
        .listener(new LogListener())
        .listener(progressTrackingChunkListener())
        .listener(stepListener())
        .build();
  }

  @Bean
  @StepScope
  public ItemStreamReader<PersonXml> xmlReader(
      @Value("#{jobParameters['FILE_PATH']}") File inputFile
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
    return reader;

    // parallelization of a chunk insert step requires synchronizing the reader
//    SynchronizedItemStreamReader<PersonXml> syncReader = new SynchronizedItemStreamReader<>();
//    syncReader.setDelegate(reader);
//    return syncReader;
  }

  @Bean
  @StepScope
  public PersonProcessor personProcessor() {
    return new PersonProcessor();
  }

  @Bean
  public <T> JpaItemWriter<T> jpaWriter() {
    JpaItemWriter<T> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(emf);
    return writer;
  }

  @Bean
  @StepScope
  public ProgressTrackingListener progressTrackingChunkListener() {
    return new ProgressTrackingListener();
  }

  @Bean
  @StepScope
  public CountingTotalItemsStepListener stepListener() {
    return new CountingTotalItemsStepListener();
  }

  @Bean
  public Step importCitiesFirstPass() {
    return stepBuilder.get("importCitiesFirstPass")
        .<PersonXml, City>chunk(100)
        .reader(xmlReader(null))
        .processor(cityMerger())
        .writer(jpaWriter())
        .listener(cityMerger())
        .build();
  }

  @Bean
  public ThreadPoolTaskExecutor batchExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("batch-");
    executor.initialize();
    return executor;
  }

  @Bean
  public CityMerger cityMerger() {
    return new CityMerger();
  }
}

