package victor.training.spring.batch.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.spring.batch.core.domain.City;
import victor.training.spring.batch.core.domain.CityRepo;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class CityMerger implements ItemProcessor<PersonXml, City>, StepExecutionListener {
   @Autowired
   private CityRepo repo;

   private Set<String> allCitiesInDB;

   @PostConstruct
   public void loadPreExistingCities() {
      allCitiesInDB = repo.findAll().stream().map(City::getName).collect(toSet());
   }
//EntityManager em;
//      em.clear();//
   @Override
   public City process(PersonXml xml) {


      if (!allCitiesInDB.contains(xml.getCity())) {
         allCitiesInDB.add(xml.getCity());
         return new City(xml.getCity());
      } else {
         return null; // skip item
      }
   }

   @Override
   public void beforeStep(StepExecution stepExecution) {
      log.info("First Pass - START");
   }

   @Override
   public ExitStatus afterStep(StepExecution stepExecution) {
      log.info("First Pass - END");
      return stepExecution.getExitStatus();   }
}
