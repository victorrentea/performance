package victor.training.performance.batch.sync;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

import static victor.training.performance.PerformanceUtil.measureCall;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
public class BatchApp {

    public static void main(String[] args) throws IOException {
        DataFileGenerator.generateFile(10_000);
        int dt = measureCall(() -> SpringApplication.run(BatchApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Step basicChunkStep() {
        // TODO optimize: tune chunk size
        return stepBuilderFactory.get("basicChunkStep")
                .<MyEntityFileRecord, MyEntity>chunk(5)
                .reader(xmlReader())
                .processor(processor())
                // TODO optimize: tune ID generation
                // TODO optimize: enable JDBC batch mode
                .writer(jpaWriter())
                .listener(new MyChunkListener())
                .listener(new MyStepExecutionListener())
                .build();
    }

    @Bean
    public MyEntityProcessor processor() {
        return new MyEntityProcessor();
    }

    @Autowired
    private EntityManagerFactory emf;

    private JpaItemWriter<MyEntity> jpaWriter() {
        JpaItemWriter<MyEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @SneakyThrows
    private ItemReader<MyEntityFileRecord> xmlReader() {
        StaxEventItemReader<MyEntityFileRecord> reader = new StaxEventItemReader<>();
        FileSystemResource inputFile = new FileSystemResource("data.xml");
        log.debug("Processing " + inputFile + " size = " + inputFile.contentLength());
        reader.setResource(inputFile);
        reader.setStrict(true);
        reader.setFragmentRootElementName("data");
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(MyEntityFileRecord.class);;
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }

    @Bean
    public Job basicJob() {
        return jobBuilderFactory.get("basicJob")
                .incrementer(new RunIdIncrementer())
                .start(basicChunkStep())
                .listener(new MyJobListener())
                .build();

    }
}

