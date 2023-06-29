package no.nav.oebs.melosys.db.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FakturaStatus {

    private String fakturaReferanseNr;
    private String fakturaNummer;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dato;
    private String status;
    private BigDecimal fakturaBelop;
    private BigDecimal ubetaltBelop;


}
