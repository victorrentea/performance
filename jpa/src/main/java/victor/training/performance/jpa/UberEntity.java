package victor.training.performance.jpa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static javax.persistence.EnumType.STRING;

@Entity
//@Data // ca de ce nu:
//- hash/equals + id = horror
//- toString care face lazy load
//- @Setter: anemic domain model
@Getter
@Setter
// singurul motiv sa faci un Builder (anti-pattern) este daca ai o structura imutabila prea pare,
// si ti-e scarba de constructor
@ToString
//un @Entity e mereu mutable, ca asa cere ORM
public class UberEntity {
    @Id
    @GeneratedValue
    private Long id;
    // BIZULE!! poate exista uber fara nume!? - nu



    @NotNull // exceptie daca e null cand: << 90% echipe folosesc asta ⭐️
    // are multi frati:  @NotBlank  @Pattern(regexp = ) @Email @Size(min=
    // a) repo.save(newEntity)
    // b) @Transactional f() { repo.fbi(id).setName(null); }
    // c) la load?!?
    // RISC:
    // 1. NU e bulletproof vs SQL direct UPDATE: ==> goto: "NOT NULL" column!
    //  -- datafix in DB Vineri 21:00, in prod, un ops speriat face UPDATE .. NAME=null where id = 1246
    //  -- migration script incorect
    //  -- alte app: nedesizirabil -> sursa de date incorecte
    // 2. Exista momente in cod in care poti vedea UberEntity{name:null}
            // u = new UberEntity().setName(dto.getName());
            // SQS.sendMessage({u.name, }); // not transacted side effect pe retea
            // emailService.send({u.name, });
            // logica complicata care va crapa cu NPE in final
            // repo.save(u); // exceptie, dar prea tarziu, userul deja a primit "Bine ai venit, null"
    // 💡 Hai sa le punem SI pe DTO si pe ENTITY incalcand flagrant (DRY principle = Don't Repeat Yourself)
    // ca sa ma asigur ca nu vad vreodata requesturi invalide. (reduc riscul #2)

    // automat daca generezi cu hibernate schema -> NOT NULL pe coloana
//    @Column(nullable = false) // adica "NOT NULL" pe coloana direct in DB

    private String name;
    private String address;
    private String city;
    private String ibanCode;
    private String cnp;
    private String ssn;
    private String passportNumber;

//    @ManyToOne
//    private Country originCountry;
    // +pastreaza FK UBER.origin_country_id ->Country
    private Long originCountryId;

    @ManyToOne
    private Country nationality;
    @ManyToOne
    private Country fiscalCountry;
    @ManyToOne
    private Country invoicingCountry;
    @ManyToOne
    private Scope scope;
    @Convert(converter = ScopeEnumConverter.class)
    private ScopeEnum scopeEnum;
    @ManyToOne
    private User createdBy;
    @Enumerated(STRING)
    private Status status;

    public enum Status {
        DRAFT, SUBMITTED, DELETED
    }


}

