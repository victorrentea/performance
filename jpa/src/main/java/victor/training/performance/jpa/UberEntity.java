package victor.training.performance.jpa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
//@Data // ca de ce nu:
//- hash/equals + id = horror
//- toString care face lazy load
//- @Setter: anemic domain model
@Getter
@Setter
// singurul motiv sa faci un Builder (anti-pattern) este daca ai o structura imutabila prea pare,
// si ti-e scarba de constructor
@ToString
//un @Entity e mereu mutable, ca asa cere ORM
public class UberEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String address;
    private String city;
    private String ibanCode;
    private String cnp;
    private String ssn;
    private String passportNumber;

    @ManyToOne
    private Country originCountry;
    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
//    @Convert(converter = ScopeEnumConverter.class)
//    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

