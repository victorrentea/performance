package victor.training.performance.batch.paritem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

import static victor.training.performance.ConcurrencyUtil.measureCall;
import static victor.training.performance.ConcurrencyUtil.sleep2;


@EnableTask
@SpringBootApplication
@EnableBatchProcessing
public class BatchParItemsApp {

    public static void main(String[] args) {
        int dt = measureCall(() -> SpringApplication.run(BatchParItemsApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    public Step sampleStep(TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("sampleStep")
                .<String, String>chunk(5)
                .reader(new StringGeneratorReader(50))
                .processor(new StringProcessor())
                .writer(new StringConsoleWriter())
                .listener(new MyChunkListener())
                .listener(new MyStepExecutionListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job parallelStepsJob() {
        return jobBuilderFactory.get("parallelFlowJob")
                .incrementer(new RunIdIncrementer())
                .start(sampleStep(taskExecutor()))
                .listener(new MyJobListener())
                .build();

    }
}

