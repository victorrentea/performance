package victor.training.performance.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import victor.training.performance.jpa.CaptureSystemOutput.OutputCapture;

import javax.persistence.EntityManager;

@SpringBootTest
//@Transactional
//@Rollback(false)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GeneratedUUID {
    @Autowired
    private EntityManager em;
    @Autowired
    private UUIDEntityRepo repo;

    @Test
    @CaptureSystemOutput
    public void assignIdentifiers(OutputCapture capture) {
        UUIDEntity entity = new UUIDEntity();
        repo.save(entity);

        System.out.println("Generated id: " + entity.getId());
        // uncomment bellow and move to option 2 in prod code to fix
//        assertThat(capture.toString()).doesNotContainIgnoringCase("SELECT");
    }
}
