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

    @ManagedOperation
    public void changeCriticalParamAtRuntime(String param) {
        System.out.println("Update param to : " + param);
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
