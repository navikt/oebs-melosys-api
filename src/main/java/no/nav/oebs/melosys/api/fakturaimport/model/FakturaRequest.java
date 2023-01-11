package no.nav.oebs.melosys.api.fakturaimport.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Klasse som definerer JSON-requestobjektet pÃ¥ PL/SQL-grensesnittet for uttrekk av konteringer.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "org_id",
        "artskonto",
        "ksted",
        "produktoppgave",
        "deloppgave",
        "fellesoppgave",
        "statskonto",
        "kilde",
        "tilsagnsaar",
        "fritt_felt_1",
        "fritt_felt_2",
        "fullmaktskode",
        "regnskapsforer",
        "system"})
public class FakturaRequest {

        private Integer org_id;
        private String artskonto;
        private String ksted;
        private String produktoppgave;
        private String deloppgave;
        private String fellesoppgave;
        private String statskonto;
        private String kilde;
        private String tilsagnsaar;
        private String fritt_felt_1;
        private String fritt_felt_2;
        private String fullmaktskode;
        private String regnskapsforer;
        private String system;
}
