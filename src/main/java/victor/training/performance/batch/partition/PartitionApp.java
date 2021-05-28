//package victor.training.performance.batch.partition;
//
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.xml.StaxEventItemReader;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
//import victor.training.performance.batch.sync.*;
//
//import javax.persistence.EntityManagerFactory;
//import java.io.IOException;
//
//@Slf4j
//@SpringBootApplication
//@EnableBatchProcessing
//@RequiredArgsConstructor
//public class PartitionApp {
//   private final JobBuilderFactory jobBuilder;
//   private final StepBuilderFactory stepBuilder;
//
//   public static void main(String[] args) throws IOException {
//      SpringApplication.run(BatchApp.class, args).close();
//   }
//
//   public Step partitionedStep() {
//      return stepBuilder.get("partitionedStep")
//          .partitioner(exportPartition())
//          .partitioner("workerStep", partitioner())
//      ;
//   }
//   @Bean
//   public MyPartitioner partitioner() {
//      return new MyPartitioner();
//   }
//   public Step exportPartition() {
//      return stepBuilder.get("exportPartition")
//
//          .<PersonXml, Person>chunk(5)
//          .reader(xmlReader())
//          .processor(personProcessor())
//          .writer(jpaWriter(null))
//          .listener(new MyChunkListener())
//          .listener(stepListener())
//          .build();
//   }
//
//   @Bean
//   @StepScope
//   public MyStepExecutionListener stepListener() {
//      return new MyStepExecutionListener();
//   }
//
//
//   @Bean
//   public PersonProcessor personProcessor() {
//      return new PersonProcessor();
//   }
//
//   @Bean
//   public JpaItemWriter<Person> jpaWriter(EntityManagerFactory emf) {
//      JpaItemWriter<Person> writer = new JpaItemWriter<>();
//      writer.setEntityManagerFactory(emf);
//      return writer;
//   }
//
//   @SneakyThrows
//   private ItemReader<PersonXml> xmlReader() {
//      StaxEventItemReader<PersonXml> reader = new StaxEventItemReader<>();
//      FileSystemResource inputFile = new FileSystemResource("data.xml");
//      reader.setResource(inputFile);
//      reader.setStrict(true);
//      reader.setFragmentRootElementName("person");
//      Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
//      unmarshaller.setClassesToBeBound(PersonXml.class);
//      reader.setUnmarshaller(unmarshaller);
//      return reader;
//   }
//
//   @Bean
//   public Job basicJob() {
//      return jobBuilder.get("basicJob")
//          .incrementer(new RunIdIncrementer())
//          .start(exportPartition())
//          .listener(new MyJobListener())
//          .build();
//
//   }
//}
//
