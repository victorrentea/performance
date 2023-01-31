package victor.training.performance.assignment.batch;

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

import static victor.training.performance.util.PerformanceUtil.measureCall;


@SpringBootApplication
@EnableBatchProcessing
public class BatchAssignmentApp {

    public static void main(String[] args) throws IOException {
        QuotationDataFileGenerator.generateFile(10_000);
        int dt = measureCall(() -> SpringApplication.run(BatchAssignmentApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Step basicChunkStep() {
        // TODO optimize: tune chunk size
        return stepBuilderFactory.get("basicChunkStep")
                .<QuotationRecord, Quotation>chunk(5)
                .reader(xmlReader())
                .processor(processor())
                // TODO optimize: tune ID generation
                // TODO optimize: enable JDBC batch mode
                .writer(jpaWriter())
                .build();
    }

    @Bean
    public QuotationToEntityTransformer processor() {
        return new QuotationToEntityTransformer();
    }

    @Autowired
    private EntityManagerFactory emf;

    private JpaItemWriter<Quotation> jpaWriter() {
        JpaItemWriter<Quotation> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    private ItemReader<QuotationRecord> xmlReader() {
        StaxEventItemReader<QuotationRecord> reader = new StaxEventItemReader<>();
        reader.setResource(new FileSystemResource("data.xml"));
        reader.setFragmentRootElementName("quotation");
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(QuotationRecord.class);;
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }

    @Bean
    public Job basicJob() {
        return jobBuilderFactory.get("basicJob")
                .incrementer(new RunIdIncrementer())
                .start(basicChunkStep())
                .build();

    }
}

