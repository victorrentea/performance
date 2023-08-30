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
    private String address;
    private String city;
    private String ibanCode;
    private String cnp;
    private String ssn;
    private String passportNumber;

//    @ManyToOne//(fetch = FetchType.LAZY)
//    private Country originCountry;

    private Long originCountryId;

//    @ManyToOne
//    private Country nationality;

    private Long nationalityId;

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

