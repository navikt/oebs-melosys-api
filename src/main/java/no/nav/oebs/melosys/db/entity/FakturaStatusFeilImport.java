package no.nav.oebs.melosys.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FakturaStatusFeilImport extends FakturaStatus {


    public FakturaStatusFeilImport(String fakturaReferanseNr, String feilmelding) {
        super(fakturaReferanseNr,
                "",
                LocalDate.now(),
                "FEIL",
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(0),
                feilmelding);

    }


}
