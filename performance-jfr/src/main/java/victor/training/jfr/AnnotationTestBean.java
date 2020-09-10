package victor.training.jfr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// <bean class="victor.training.jfr.TrickyProcess" />

@Service
class TrickyProcess {
    private boolean enableMoreLogging;

    public TrickyProcess setEnableMoreLogging(boolean enableMoreLogging) {
        this.enableMoreLogging = enableMoreLogging;
        return this;
    }

    @Scheduled(fixedRate = 1000)
    public void myProcess() {
        if (enableMoreLogging) {
            System.out.println("STUFF by boolean");
        }
    }
}



@ManagedResource(
        objectName="bean:name=testBean4",
        description="My Managed Bean",
        log=true,
        logFile="jmx.log",
        currencyTimeLimit=15,
        persistPolicy="OnUpdate",
        persistPeriod=200,
        persistLocation="foo",
        persistName="bar")
@Component
public class AnnotationTestBean implements IJmxTestBean {

    private String name;
    private int age;

    @Autowired
    private TrickyProcess process;

    @ManagedOperation
    public void setProcessLogging(boolean enabled) {
        process.setEnableMoreLogging(enabled);
    }

    @ManagedAttribute(description="The Age Attribute", currencyTimeLimit=15)
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @ManagedAttribute(description="The Name Attribute",
            currencyTimeLimit=20,
            defaultValue="bar",
            persistPolicy="OnUpdate")
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(defaultValue="foo", persistPeriod=300)
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

    public void dontExposeMe() {
        throw new RuntimeException();
    }

}