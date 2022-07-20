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

    @ManyToOne
    // !! se incarca eager cu JOIN catre ele daca faci findById
    // si li se incarca lor toate atributele..
    // Mai rau daca si ele au mai departe @ManyToOne catre altii, se incarca si aia..
    // prea multe JOINuri

    // !! se incarca cu SELECT separat daca e adus cu JQPL / findAll
    // prea multa retea

    // la ambele; de ce aduc si country daca n-am nevoie de el.
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

