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

    // doar pt referentiale date statice comune
//    @ManyToOne
//    private Country originCountry;
    private Long originCountryId; // Asta nu inseamna ca dau jos si FK, p-ala il las

    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;
    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }

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


    public UberEntity setNationality(Country nationality) {
        this.nationality = nationality;
        return this;
    }

    public UberEntity setFiscalCountry(Country fiscalCountry) {
        this.fiscalCountry = fiscalCountry;
        return this;
    }

    public UberEntity setInvoicingCountry(Country invoicingCountry) {
        this.invoicingCountry = invoicingCountry;
        return this;
    }

    public UberEntity setScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public UberEntity setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }
}

