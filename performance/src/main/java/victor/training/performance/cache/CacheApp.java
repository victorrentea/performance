package victor.training.performance.cache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import javax.annotation.PostConstruct;
import java.io.*;

@EnableCaching
@SpringBootApplication
public class CacheApp {
    public static void main(String[] args) {
        SpringApplication.run(CacheApp.class);
    }
}

@RequiredArgsConstructor
@RestController
class CashResource {
    private final CashProvider cashProvider;
    @GetMapping("cash")
    public String getCash() {
        return cashProvider.getCash();
    }
    @GetMapping("increase")
    public void increaseSalary() {
        cashProvider.increaseSalary();
    }
}

@Service
@RequiredArgsConstructor
class CashProvider {
    private final EmployeeRepo employeeRepo;

    @Cacheable("salary")
    public String getCash() {
        return employeeRepo.getSalary() + "";
    }
    @CacheEvict("salary")
    public void increaseSalary() {
        employeeRepo.increaseSalary();
    }
}
@Service
class EmployeeRepo {

    public static final File FILE = new File("salary.txt");

    @PostConstruct
    @SneakyThrows
    public void initFile() {
        if (!FILE.exists()) {
            try (Writer writer = new FileWriter(FILE)) {
                IOUtils.write("1000", writer);
            }
        }
        System.out.println("Created file at " + FILE.getAbsolutePath());
    }
    public void increaseSalary() {
        System.out.println("Increasing salary");
        int old = read();
        write(old + 500);
    }

    @SneakyThrows
    private void write(int newSalary) {
        try (FileWriter writer = new FileWriter(EmployeeRepo.FILE)) {
            IOUtils.write("" + newSalary, writer);
        }
    }

    @SneakyThrows
    public int getSalary() {
        ConcurrencyUtil.sleepq(3000);

        return read();
    }

    @SneakyThrows
    private int read() {
        try (Reader reader = new FileReader(EmployeeRepo.FILE)) {
            return Integer.parseInt(IOUtils.toString(reader));
        }
    }
}
