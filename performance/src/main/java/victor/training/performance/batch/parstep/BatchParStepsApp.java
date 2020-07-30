package victor.training.performance.batch.parstep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@SpringBootApplication
@EnableBatchProcessing
public class BatchParStepsApp {

    public static void main(String[] args) {
        SpringApplication.run(BatchParStepsApp.class, args).close();
    }

    @Autowired
    JobBuilderFactory jobBuilderFactory;
 
    @Autowired
    StepBuilderFactory stepBuilderFactory;
 
    private TaskletStep taskletStep(String step) {
        return stepBuilderFactory.get(step).tasklet(new DummyTasklet(step)).build();
    }

    @Bean
    public Job parallelStepsJob() {
        Flow masterFlow = new FlowBuilder<Flow>("masterFlow").start(taskletStep("step1")).build();

        Flow flowJob1 = new FlowBuilder<Flow>("flow1").start(taskletStep("step2")).build();
        Flow flowJob2 = new FlowBuilder<Flow>("flow2").start(taskletStep("step3")).build();
        Flow flowJob3 = new FlowBuilder<Flow>("flow3").start(taskletStep("step4")).build();

        Flow slaveFlow = new FlowBuilder<Flow>("slaveFlow")
                .split(new SimpleAsyncTaskExecutor()).add(flowJob1, flowJob2, flowJob3).build();
 
        return (jobBuilderFactory.get("parallelFlowJob")
                .incrementer(new RunIdIncrementer())
                .start(masterFlow)
                .next(slaveFlow)
                .build()).build();
 
    }
 
}