package victor.training.performance.batch.paritem;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

import static victor.training.performance.ConcurrencyUtil.*;

@SpringBootApplication
@EnableBatchProcessing
public class BatchParItemsApp {

    public static void main(String[] args) {
        int dt = measureCall(() -> SpringApplication.run(BatchParItemsApp.class, args).close());
        System.out.println("Batch took " + dt + " ms");
    }

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Step sampleStep(TaskExecutor taskExecutor) {
        return this.stepBuilderFactory.get("sampleStep")
                .<String, String>chunk(10)
                .reader(new StringGeneratorReader(10))
                .processor(new StringProcessor())
                .writer(new StringConsoleWriter())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job parallelStepsJob() {
        return jobBuilderFactory.get("parallelFlowJob")
                .incrementer(new RunIdIncrementer())
                .start(sampleStep(taskExecutor()))
                .build();

    }
}

class StringProcessor implements ItemProcessor<String, String> {
    @Override
    public synchronized String process(String item) {
        log("Start processing " + item);
        sleep2(100);
        log("End processing " + item);
        return item.toUpperCase();
    }
}


class StringGeneratorReader implements ItemReader<String> {
    private int count;
    public StringGeneratorReader(int count) {
        this.count = count;
    }
    public String read() {
        if (count <= 0) {
            return null;
        }
        count--;
        String s = RandomStringUtils.random(4);
        log("Read " + s);
        sleep2(1);
        return s;
    }
}

class StringConsoleWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> list) throws Exception {
        sleep2(1);
        for (String s : list) {
            log("Write " + s);
        }
    }
}

