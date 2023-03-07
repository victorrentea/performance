package victor.training.performance.jpa;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Data
//@ForeignKey("") // keep the FK
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

//    @ManyToOne
//    private Country originCountry;
    // or, more efficient:
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

