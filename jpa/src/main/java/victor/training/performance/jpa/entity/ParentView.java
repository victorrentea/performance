package victor.training.performance.jpa.entity;

import lombok.Getter;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity // mapped to PARENT_VIEW
@Immutable // shouldn't ever be UPDATEd
@Getter
public class ParentView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;
}
