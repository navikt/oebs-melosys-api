package no.nav.oebs.melosys.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FakturaStatus {

    private BigDecimal fodselsnummer;
    private String vedtaksId;
    private String fakturaReferanseNr;
    private BigDecimal belop;
    private String status;

}
