package no.nav.oebs.melosys.db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class FakturaTest {

    private BigDecimal fodselsnummer;
    private String fullmektigOrgnr;
    private BigDecimal fullmektigFnr;
    private String vedtaksId;
    private String fakturaReferanseNr;
    private String kreditReferanseNr;
    private String referanseBruker;
    private String referanseNAV;
    private String beskrivelse;
    private List<FakturaLinje> fakturaLinjer;
    private LocalDate faktureringsDato;

}
