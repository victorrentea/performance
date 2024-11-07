package victor.training.performance.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Clob;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Data
public class Uber {
    @Id
//    @GeneratedUUID// .save(entity{id=null}) => generator is called to create an id
//    private String id;
    private String id = UUID.randomUUID().toString(); // .save(entity{id!=null}) => .merge() => +1 SELECT before every INSERT

    private String name;
    private String address;
    private String city;
    private String ibanCode;
    private String cnp;
    private String ssn;
    private String passportNumber;

    @ManyToOne
    private Country originCountry;

    // make excessive use of object links between @Entity in a model can degrade performance
//    @ManyToOne//(fetch = FetchType.LAZY) // #1 option 1 > more magic
//    private Country nationality;

    // #2 option more towards traditional modeling
    private Long nationalityId;

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
    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }
    @Enumerated(STRING)
    private Status status;

    @Lob
//    private char[] content;
    private Clob content;
}

