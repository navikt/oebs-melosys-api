package no.nav.oebs.melosys.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FakturaStatusFeilImport extends FakturaStatus {
    String feilmelding;

    public FakturaStatusFeilImport(String fakturaReferanseNr, String feilmelding) {
        super(fakturaReferanseNr,
                "",
                LocalDateTime.now(),
                "FEIL",
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(0));
        this.feilmelding = feilmelding;
    }


}
