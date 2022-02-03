package victor.training.performance.jpa;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Data
public class UberEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String ibanCode;
    private String cnp;
    private String ssn;
    private String passportNumber;

    // am inlocuit relatii JPA catre date 'statice' (nomenclatoare, dictionare, referentiale)
    // cu Long + FK in DB
    // PRET: un pic mai complicat cand vrei numele tarii (eg) ainevoie de JOIN
    // Mai putin OOP (mai putin navigabil?)

//    @ManyToOne()
//    private Country originCountry;
//    @Mapped
    private Long originCountryId;
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

