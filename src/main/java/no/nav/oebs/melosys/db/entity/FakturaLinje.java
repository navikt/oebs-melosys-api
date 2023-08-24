package no.nav.oebs.melosys.db.entity;


import lombok.*;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class FakturaLinje {

    private String beskrivelse;
    private Double antall;
    private BigDecimal inntekt;
    private String dekning;
    private String sats;
    private BigDecimal enhetspris;
    private BigDecimal belop;

    public FakturaLinje(int i) {

    }

}
