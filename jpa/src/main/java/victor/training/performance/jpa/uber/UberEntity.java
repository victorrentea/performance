package victor.training.performance.jpa.uber;

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

    @ManyToOne //a) lazy
    private Country originCountry;
    //b) id numeric in loc de jpa link
//    private Long originCountryId;

    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
//    @Convert(converter = ScopeEnumConverter.class) // store 1 letter code
//    @Enumerated(STRING) // store enum name
//    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

