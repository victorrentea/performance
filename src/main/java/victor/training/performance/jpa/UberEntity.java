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
    private String firstName, lastName, ibanCode, cnp, ssn, passportNumber;


    private Long originCountryId; // Pastrezi FK din baza

//    @ManyToOne//(fetch = FetchType.LAZY)
//    private Country originCountry;

//    public Country getOriginCountry() { // acum va fi hackuit de Hibernate LA COMPILARE
//        if (!originCountryLoaded) {
//            originCountry = load din db;
//            originCountryLoaded = true;
//        }
//        return originCountry;
//    }

    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
//    @Enumerated(STRING)
//    private ScopeEnum scope;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

