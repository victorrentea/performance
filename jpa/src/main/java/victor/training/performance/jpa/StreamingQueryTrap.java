package victor.training.performance.jpa;


import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

public class StreamingQueryTrap {

  private final ChildRepo childRepo;
  private final EntityManager entityManager;

  public StreamingQueryTrap(ChildRepo childRepo, EntityManager entityManager) {
    this.childRepo = childRepo;
    this.entityManager = entityManager;
  }

  @Transactional
  public void method() {
    Stream<Child> stream = childRepo.findChildByParentId(1L);

    // Out Of Memory with the hibernate.Session (~Persistence Context) of 2.5 gb of fram
    stream
            .peek(e-> entityManager.detach(e)) // tell Hib to remove from the 1st level cache the entity
            .forEach(c -> System.out.println(c.toString()));
  }
}
