package victor.training.performance.leaks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@ManagedResource(
        objectName="bean:name=testBean4",
        description="My Managed Bean")
@Component
public class CustomMBean {
    private String name;
    private int age;

    @Autowired
    private TrickyProcess process;

    @ManagedOperation
    public void setCollectMetrics(boolean enabled) {
        process.setCollectMetrics(enabled);
    }

    @ManagedAttribute
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(defaultValue="foo")
    public String getName() {
        return name;
    }

    @ManagedOperation(description="Add two numbers")
    @ManagedOperationParameters({
        @ManagedOperationParameter(name = "x", description = "The first number"),
        @ManagedOperationParameter(name = "y", description = "The second number")})
    public int add(int x, int y) {
        return x + y;
    }
}

@Service
class TrickyProcess {
    private boolean collectMetrics;

    public void setCollectMetrics(boolean collectMetrics) {
        this.collectMetrics = collectMetrics;
    }

    @Scheduled(fixedRate = 1000)
    public void myProcess() {
        if (collectMetrics) {
            System.out.println("Collect metrics");
        }
    }
}