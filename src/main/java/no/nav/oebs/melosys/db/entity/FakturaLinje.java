package no.nav.oebs.melosys.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
//@Table(name = "table_name")
public class FakturaLinje {

    @Id
    // generator
    private Long id;

    @Column(name = "fakturaId")
    private Long fakturaId;

    @Column(name = "FAKTURALINJEBESKRIVELSE")
    private String beskrivelse;

    @Column(name = "ANTALL")
    // hvordan fungerer kreditering? antal negativt antall
    private int antall;

    @DecimalMin(value = "0.0")
    @Column(name = "ENHETSPRIS")
    private BigDecimal enhetspris;

    @Column(name = "BELOP")
    private BigDecimal belop;

}
