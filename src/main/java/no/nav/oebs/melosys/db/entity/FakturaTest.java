package no.nav.oebs.melosys.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class FakturaTest {

    private String fodselsnummer;
    private String fullmektigOrgnr;
    private BigDecimal fullmektigFnr;
    @JsonAlias("vedtaksId")
    private String fakturaserieReferanse;
    private String fakturaReferanseNr;
    private String kreditReferanseNr;
    private String referanseBruker;
    private String referanseNAV;
    private String beskrivelse;
    private String artikkel;
    private List<FakturaLinje> fakturaLinjer;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate faktureringsDato;

}
