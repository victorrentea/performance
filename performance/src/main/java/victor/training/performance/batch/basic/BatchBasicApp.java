package victor.training.performance.batch.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
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

import static victor.training.performance.ConcurrencyUtil.measureCall;


@SpringBootApplication
@EnableBatchProcessing
public class BatchBasicApp {

    public static void main(String[] args) throws IOException {
        DataFileGenerator.generateFile(10_000);
        int dt = measureCall(() -> SpringApplication.run(BatchBasicApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory emf;


    // TODO define a chunk-based step
    // reader: StaxEventItemReader; pass the FileSystemResource;
    // processor: convert PersonXml -> Person
    // writer: JpaItemWriter

    // TODO tryout listeners

    private TaskletStep dummyTaskStep(String step) {
        return stepBuilderFactory.get(step).tasklet(new DummyTasklet(step)).build();
    }
    @Bean
    public Job basicJob() {
        return jobBuilderFactory.get("basicJob")
                .incrementer(new RunIdIncrementer())
                .start(dummyTaskStep("Aloha Spring Batch!"))
                .build();

    }
}

