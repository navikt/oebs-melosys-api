package no.nav.oebs.melosys.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.oebs.melosys.kafka.CustomLocalDateDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FakturaStatus {

    private String fakturaReferanseNr;
    @JsonAlias("fakturanummer")
    private String fakturaNummer;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate dato;
    private String status;
    @JsonAlias("fakturabeløp")
    private BigDecimal fakturaBelop;
    @JsonAlias("ubetaltbeløp")
    private BigDecimal ubetaltBelop;
    private String feilmelding;

}
