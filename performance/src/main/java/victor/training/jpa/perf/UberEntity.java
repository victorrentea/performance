package victor.training.jpa.perf;

import javax.persistence.*;

@Entity
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
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String cv;

    private String passportNumber;
//    @ManyToOne
    private Long originCountryId;
//    @ManyToOne
    private Long nationalityId;
//    @Transient
//    private Country fiscalCountry;
    private Long fiscalCountryId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Country invoicingCountry;
//    private Long invoicingCountryId;
//    @ManyToOne
    private Long scopeId;
    @ManyToOne
    private User createdBy;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UberEntity setName(String name) {
        this.name = name;
        return this;
    }

    public UberEntity setCv(String cv) {
        this.cv = cv;
        return this;
    }

    public String getCv() {
        return cv;
    }
    //    public UberEntity setOriginCountry(Country originCountry) {
//        this.originCountry = originCountry;
//        return this;
//    }
//
//    public UberEntity setNationality(Country nationality) {
//        this.nationality = nationality;
//        return this;
//    }
//
//    public UberEntity setFiscalCountry(Country fiscalCountry) {
//        this.fiscalCountry = fiscalCountry;
//        return this;
//    }
//
//    public UberEntity setInvoicingCountry(Country invoicingCountry) {
//        this.invoicingCountry = invoicingCountry;
//        return this;
//    }
//
//    public UberEntity setScope(Scope scope) {
//        this.scope = scope;
//        return this;
//    }


    public Long getOriginCountryId() {
        return originCountryId;
    }

    public UberEntity setOriginCountryId(Long originCountryId) {
        this.originCountryId = originCountryId;
        return this;
    }

    public Long getNationalityId() {
        return nationalityId;
    }

    public UberEntity setNationalityId(Long nationalityId) {
        this.nationalityId = nationalityId;
        return this;
    }

    public Long getFiscalCountryId() {
        return fiscalCountryId;
    }

    public UberEntity setFiscalCountryId(Long fiscalCountryId) {
        this.fiscalCountryId = fiscalCountryId;
        return this;
    }

//    public Long getInvoicingCountryId() {
//        return invoicingCountryId;
//    }
//
//    public UberEntity setInvoicingCountryId(Long invoicingCountryId) {
//        this.invoicingCountryId = invoicingCountryId;
//        return this;
//    }


    public Country getInvoicingCountry() {
        return invoicingCountry;
    }

    public UberEntity setInvoicingCountry(Country invoicingCountry) {
        this.invoicingCountry = invoicingCountry;
        return this;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public UberEntity setScopeId(Long scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    public UberEntity setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }
}

@Entity
class Country {
    @Id
    private Long id;
    private String name;
    private String region;
    private String continent;
    private int population;
    protected Country() {
    }
    public Country(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
@Entity
class Scope {
    @Id
    private Long id;
    private String name;
    private Scope() {
    }
    public Scope(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
}

@Entity
class User {
    @Id
    private Long id;
    private String name;
    private User() {
    }
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}