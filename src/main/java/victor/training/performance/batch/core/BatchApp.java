package victor.training.performance.batch.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import victor.training.performance.batch.core.domain.Person;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;

import static victor.training.performance.util.PerformanceUtil.measureCall;

@EnableCaching
@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchApp {
   private final JobBuilderFactory jobBuilder;
   private final StepBuilderFactory stepBuilder;

   public static void main(String[] args) throws IOException {
      XmlFileGenerator.generateFile(10_000);
      int dt = measureCall(() -> SpringApplication.run(BatchApp.class, args).close());
      System.out.println("Batch took " + dt + " ms");
   }

   @Bean
   public TaskExecutor taskExecutor(){
      return new SimpleAsyncTaskExecutor("spring_batch");
   }


   @Bean
   public Job basicJob() {
      return jobBuilder.get("basicJob")
          .incrementer(new RunIdIncrementer())
          .start(basicChunkStep())
          .listener(new MyJobListener())
          .build();
   }

   @Bean
   public Step basicChunkStep() {
      return stepBuilder.get("basicChunkStep")
          .<PersonXml, Person>chunk(50)
          .reader(xmlReader(null))
          .processor(personProcessor())
          .writer(jpaWriter(null))
//          .listener(progressTrackingChunkListener())
//          .listener(logFirstChunkListener())
//          .listener(stepListener())
          .build();
      // TODO optimize: run insert in multithread. > SynchronizedItemStreamReader
   }

   private ChunkListener logFirstChunkListener() {
      return new LogFirstChunkListener();
   }

//   @Bean
//   @StepScope
//   public ProgressTrackingChunkListener progressTrackingChunkListener() {
//      return new ProgressTrackingChunkListener();
//   }

//   @Bean
//   @StepScope
//   public CountingTotalItemsStepListener stepListener() {
//      return new CountingTotalItemsStepListener();
//   }

   @Bean
   public PersonProcessor personProcessor() {
      return new PersonProcessor();
   }

   @Bean
   public JpaItemWriter<Person> jpaWriter(EntityManagerFactory emf) {
      JpaItemWriter<Person> writer = new JpaItemWriter<>();
      writer.setEntityManagerFactory(emf);
      return writer;
   }

   @Bean
   @StepScope
   public ItemStreamReader<PersonXml> xmlReader(
         @Value("#{jobParameters['FILE_PATH']}") File inputFile
   ) {
      inputFile = new File("data.xml");
      log.info("Reading file from: {}", inputFile);
      if (!inputFile.exists()) throw new IllegalArgumentException("Not Found: " + inputFile);
      StaxEventItemReader<PersonXml> reader = new StaxEventItemReader<>();
      reader.setResource(new FileSystemResource(inputFile));
      reader.setStrict(true);
      reader.setFragmentRootElementName("person");
      Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
      unmarshaller.setClassesToBeBound(PersonXml.class);
      reader.setUnmarshaller(unmarshaller);
      return reader;

//      SynchronizedItemStreamReader<PersonXml> syncReader = new SynchronizedItemStreamReader<>();
//      syncReader.setDelegate(reader);
//      return syncReader;
   }


}

