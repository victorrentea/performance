package victor.training.performance.jpa.uber;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Data
// entitate naiva OOP, modelata de un student ce abia s-a incantat cu OOP la scoala
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

//    @ManyToOne
//    private Country nationality; // in tot codul din BE
    private Long nationalityId; // gata cu JOINURILE inutile, SELECTURILE aiurea pe care le face JPA JPA
    // de fiecare data cand accessez getNationatlity().getId()
    // + PASTRAM FK intre tabele, ca sa nu avem probleme cu integritatea datelor!!!
//    @ManyToOne
//    private Country fiscalCountry;

    private Long fiscalCountryId; // + FK

    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
//    @Convert(converter = ScopeEnumConverter.class) // store 1 letter code
//    @Enumerated(STRING) // store enum name
//    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING) // sa nu te prind cu @Entity UberStatus cu valori fixate in DB.
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

