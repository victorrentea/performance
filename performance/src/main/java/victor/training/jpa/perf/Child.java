package victor.training.jpa.perf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private LocalDate createDate;

    private Child() {
    }

    public Child(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Child setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
        return this;
    }
}
