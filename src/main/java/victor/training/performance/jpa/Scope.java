package victor.training.performance.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

// GLOBAL, REGION, COUNTRY, AREA, CITY - valori finite
// INSERT into SCOPE(1,'GLOBAL')
// INSERT into SCOPE(2,'COUNTRY')

// State {DRAFT,ACTIVE,DELETED}
// INSERT into State(1,'DRAFt')
// INSERT into State(2,'ACTIVE')

// Daca vreodata in cod faci if (uber.status = Enum.ACTIVE)
// AICI STOP JOC !!!
// ASTAZI DISTRUGI TABELA


 enum ScopeEnum {
   GLOBAL, REGION, COUNTRY, AREA, CITY
}
@Entity
public class Scope {
   @Id
   private Long id;
   private String name;

   private Scope() {
   }

   public Scope(Long id, String name) {
      this.id = id;
      this.name = name;
   }
}
