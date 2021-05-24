package victor.training.performance.assignment.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Slf4j
@SpringBootApplication
public class SearchAssignmentApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SearchAssignmentApp.class);
    }

    @Autowired
    EntityManager em;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        List<String> makes = Arrays.asList("Ford", "Toyota", "Honda", "Mazda", "Suzuki");
        List<String> allFeatureNames = Arrays.asList("Electric Windows", "ABS", "ELS", "GPS", "Parking sensors");
        Set<CarFeature> allFeatures = allFeatureNames.stream().map(CarFeature::new).collect(toSet());
        allFeatures.forEach(em::persist);

        Random r = new Random();
        for (String make : makes) {
            for (int year = 2000; year < 2019; year++) {
                List<CarFeature> features = new ArrayList<>(allFeatures);
                Collections.shuffle(features);
                Set<CarFeature> featureSet = new HashSet<>(features.subList(0, r.nextInt(features.size())));
                Car car = new Car();
                car.setMake(make);
                car.setYear(year);
                car.setFeatures(featureSet);
                car.setOwners(new HashSet<>(Arrays.asList(new CarOwner(), new CarOwner())));
                em.persist(car);
            }
        }
        log.debug("Persisted dummy data");
    }
}
