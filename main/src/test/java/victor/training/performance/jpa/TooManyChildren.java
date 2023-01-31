package victor.training.performance.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static victor.training.performance.util.PerformanceUtil.printUsedHeap;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class TooManyChildren {
   private static final int DEVICES_FOR_DEFAULT = 100_000;
   @Autowired
   private TagRepo tagRepo;
   @Test
   @Sql(statements = {
       "INSERT INTO TAG(ID, NAME) VALUES (1, 'DEFAULT'), (2, 'SMART_PHONE'), (3, 'SMART_TV')",
       "INSERT INTO DEVICE(ID, NAME, TAG_ID) SELECT X, 'Device ' || X, 1 FROM SYSTEM_RANGE(1, "+ DEVICES_FOR_DEFAULT+ ")" //all are DEFAULT
   })
   void test() {
      printUsedHeap("Start");
      Tag defaultTag = tagRepo.findById(1L).get();
      defaultTag.getDevices().size(); // trigger lazy load
      printUsedHeap("With Tag loaded with HUGE children list");
      System.out.println(defaultTag);
   }
}


@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@Entity
class Device {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   @ManyToOne
   private Tag tag;

}

@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@Entity
class Tag {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   @OneToMany(mappedBy = "tag")
   private List<Device> devices = new ArrayList<>();
}

interface TagRepo extends JpaRepository<Tag, Long> {

}

interface DeviceRepo extends JpaRepository<Device, Long> {

}