package no.nav.oebs.melosys.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDate;

/**
 * Klasse som definerer JSON-requestobjektet pÃ¥ PL/SQL-grensesnittet for uttrekk av konteringer.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "faktura_id", "fakturaref", "fakturadato", "lastupdatedate" })
public class FakturaRequest {

    private Integer faktura_id;

    private String fakturaref;

    private LocalDate fakturadato;

    private LocalDate lastupdatedate;
}

