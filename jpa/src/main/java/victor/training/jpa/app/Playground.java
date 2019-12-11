package victor.training.jpa.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import victor.training.jpa.app.domain.entity.Teacher;
import victor.training.jpa.app.repo.TeacherRepo;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

import static java.util.Arrays.asList;

@Service
public class Playground {
    public static final Logger log = LoggerFactory.getLogger(Playground.class);
    @Autowired
    private EntityManager em;

    @Transactional
    public void firstTransaction() {
        log.debug("Halo!");
//        List<Teacher> teachers = em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
        List<Teacher> teachers = asList(em.find(Teacher.class, 1L));
        System.out.println("Teachers : " + teachers);
        System.out.println(teachers.get(0).getDetails().getClass());
        System.out.println(teachers.get(0).getDetails().getCv());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void secondTransaction() {
        log.debug("Halo2!");
    }
}
