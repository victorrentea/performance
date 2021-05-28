package victor.training.performance.batch.partitioning;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.batch.sync.domain.City;
import victor.training.performance.batch.sync.domain.Person;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class PersistDataBeforeJob implements org.springframework.batch.core.JobExecutionListener {
   public static final int N = 1000;
   @Autowired
   private EntityManager em;

   @Override
   @Transactional(propagation = Propagation.REQUIRES_NEW) // DONT DO THIS !
   public void beforeJob(JobExecution jobExecution) {
      System.out.println("INSERTING DATA");
      List<City> cities = new ArrayList<>();
      for (int i = 0; i < N / 1000; i++) {
         City city = new City("City" + i);
         em.persist(city);
         cities.add(city);
      }
      for (int i = 0; i < N; i++) {
         Person person = new Person("Name" + i);
         person.setCity(cities.get(i/1000));
         em.persist(person);
      }
      System.out.println("INSERTED DATA");
   }

   @Override
   public void afterJob(JobExecution jobExecution) {

   }
}
