package victor.training.performance.jpa.uber;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity // naive OOP modeling
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

//    @ManyToOne
//    private Country originCountry;
    // Descoperire: in multe proiecte nu ai nevoie vreodata decat de ID/ISO
//    prin codul java
    private Long originCountryId;
    // NB: va rog 🙏 pastrati FK cu COUNTRY

    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
//    @ManyToOne
//    private Scope scope;
//    @Convert(converter = ScopeEnumConverter.class)
    @Enumerated(STRING)
    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
}

