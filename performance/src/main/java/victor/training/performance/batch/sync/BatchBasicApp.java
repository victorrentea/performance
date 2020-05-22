package victor.training.performance.batch.sync;

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
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import victor.training.performance.batch.paritem.StringConsoleWriter;

import javax.persistence.EntityManagerFactory;

import static victor.training.performance.ConcurrencyUtil.measureCall;


@EnableTask
@SpringBootApplication
@EnableBatchProcessing
public class BatchBasicApp {

    public static void main(String[] args) {
        int dt = measureCall(() -> SpringApplication.run(BatchBasicApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Step basicChunkStep() {
        return stepBuilderFactory.get("basicChunkStep")
                .<MyEntity, MyEntity>chunk(5)
                .reader(xmlReader())
                .processor(new MyEntityProcessor())
                .writer(jpaWriter())
                .listener(new MyChunkListener())
                .listener(new MyStepExecutionListener())
                .build();
    }

    @Autowired
    private EntityManagerFactory emf;

    private JpaItemWriter<MyEntity> jpaWriter() {
        JpaItemWriter<MyEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    private ItemReader<MyEntity> xmlReader() {
        StaxEventItemReader<MyEntity> reader = new StaxEventItemReader<>();
        reader.setResource(new FileSystemResource("data.xml"));
        reader.setFragmentRootElementName("data");
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(MyEntity.class);;
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

