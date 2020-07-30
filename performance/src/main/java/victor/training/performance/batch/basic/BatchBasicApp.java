package victor.training.performance.batch.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Unmarshaller;
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
    // listener: set filename on execution context

    private TaskletStep dummyTaskStep(String step) {
        return stepBuilderFactory.get(step)
            .tasklet(new DummyTasklet(step))
            .build();
    }

    @Value("${file.path}")
    FileSystemResource inputFile;

    @Value("${chunk.size:50}")
    private int chunkSize;

    private Step chunkStep() {
        System.out.println("inputFile = " + inputFile); // TODO
        return stepBuilderFactory.get("itemStep")
                .<PersonXml, Person>chunk(chunkSize)
            // = dimensiunea Tranzactiei. face COMMIT dupa fiecare pagina
                    // FOARTE IMPORTANT sa in experimentezi pe date reale pe un mediu cat mai aproape de caracteristicile de Prod
                .reader(xmlReader())
                .processor(converter())
                .writer(jpaWriter())
                .build();

    }

    private ItemWriter<Person> jpaWriter() {
        // TODO log ce scriu
        JpaItemWriter<Person> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    @StepScope
    public ItemProcessor<PersonXml, Person> converter() {
        return new PersonConverter();
    }

    private ItemReader<PersonXml> xmlReader() {
        StaxEventItemReader<PersonXml> reader = new StaxEventItemReader<>();
        reader.setResource(inputFile);
        reader.setFragmentRootElementName("person");
        Jaxb2Marshaller u = new Jaxb2Marshaller();
        u.setClassesToBeBound(PersonXml.class);
        reader.setUnmarshaller(u);
        return reader;
    }

    @Bean
    public Job basicJob() {
        return jobBuilderFactory.get("basicJob")
                .incrementer(new RunIdIncrementer())
                .start(dummyTaskStep("Unzip input file"))
                .next(dummyTaskStep("Verify File"))
                .next(dummyTaskStep("Send notification emails"))
                .next(chunkStep())
                .next(dummyTaskStep("Move files to /done folder"))
                .listener(getListener())
                .build();

    }

    @Bean
    public MyJobListener getListener() {
        return new MyJobListener();
    }


}

