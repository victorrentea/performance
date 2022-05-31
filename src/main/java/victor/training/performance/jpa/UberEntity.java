package victor.training.performance.jpa;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.EnumType.STRING;

// aici merita
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//
//abstract class Record {
//}
//class PersonRecord extends Record {
//    // cnp
//}
//class ShipRecord extends Record {
//    // flag
//}

@Entity
@Data
public class UberEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String firstName, lastName, ibanCode, cnp, ssn, passportNumber;

//    @ManyToOne// naivitate OOP (ani de liceu)
//    private Country originCountry;
    private Long originCountryId; // Lasi FK in DB
    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne(fetch = FetchType.LAZY)
    private Scope scope;

//    @Convert(converter = ScopeEnumConverter.class)
////    @Enumerated(STRING) // must-have. Daca nu faci, scrie in baza 0 pt GLOBAL << NICIODATA
//    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

