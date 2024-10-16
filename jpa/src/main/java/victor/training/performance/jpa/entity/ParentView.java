package victor.training.performance.jpa.entity;

import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable
@Getter
@Table(name="PARENT_VIEW")
public class ParentView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;
}
