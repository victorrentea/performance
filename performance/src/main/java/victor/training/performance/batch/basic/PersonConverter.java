package victor.training.performance.batch.basic;

import org.springframework.batch.item.ItemProcessor;

public class PersonConverter implements ItemProcessor<PersonXml, Person> {
   @Override
   public Person process(PersonXml item) throws Exception {
      Person entity = new Person();
      entity.setName(item.getName());
      return entity;
   }
}
