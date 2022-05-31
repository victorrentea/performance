package victor.training.performance.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@EnableCaching
@RestController
@SpringBootApplication
public class JpaPerfApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(JpaPerfApplication.class, args);
    }



    @Autowired
    private CountryRepo countryRepo;
    @GetMapping("countries")
    public List<Country> method() {
        return countryRepo.findAll();
    }

    @Override
    public void run(String... args) throws Exception {

        countryRepo.save(new Country("RO"));
        countryRepo.save(new Country("UA"));
    }
}
