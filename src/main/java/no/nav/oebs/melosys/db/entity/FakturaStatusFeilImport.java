package no.nav.oebs.melosys.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@NoArgsConstructor
public class FakturaStatusFeilImport extends FakturaStatus {


    public FakturaStatusFeilImport(String fakturaReferanseNr, String feilmelding) {
        super(fakturaReferanseNr,
                "",
                LocalDate.now(ZoneId.systemDefault()),
                "FEIL",
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(0),
                feilmelding);

    }


}
