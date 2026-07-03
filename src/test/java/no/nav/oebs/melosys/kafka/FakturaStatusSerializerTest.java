package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.common.utils.ObjektMaps;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FakturaStatusSerializerTest {

    private FakturaStatusSerializer serializer;
    private ObjektMaps objektMaps;

    @BeforeEach
    void setUp() {
        serializer = new FakturaStatusSerializer();
        objektMaps = new ObjektMaps(new JsonMapper());
    }

    @Test
    void serialize_nullData_returnsNull() {
        assertThat(serializer.serialize("topic", null)).isNull();
    }

    void serialize_validFakturaStatus_roundTripForUtvalgteFelterUtenDato() {
        status.setFakturaReferanseNr("REF-001");
        status.setFakturaNummer("12345");
        status.setStatus("SENDT");
        status.setFakturaBelop(new BigDecimal("1000.00"));

        FakturaStatus deserialized = objektMaps.toObject(new String(result, java.nio.charset.StandardCharsets.UTF_8), FakturaStatus.class);
        assertThat(deserialized.getFakturaReferanseNr()).isEqualTo("REF-001");
        assertThat(deserialized.getFakturaNummer()).isEqualTo("12345");
        assertThat(deserialized.getStatus()).isEqualTo("SENDT");
        assertThat(deserialized.getFakturaBelop()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void serialize_dato_serialiseresMedBindestrekFormat() {
        // @JsonFormat(pattern="dd-MM-yyyy") styrer ut-formatet.
        // CustomLocalDateDeserializer forventer dd.MM.yyyy inn — asymmetri er bevisst av design.
        FakturaStatus status = new FakturaStatus();
        status.setDato(LocalDate.of(2026, 1, 15));

        assertThat(new String(result, java.nio.charset.StandardCharsets.UTF_8)).contains("\"dato\":\"15-01-2026\"");
    }

    @Test
    void serialize_emptyFakturaStatus_returnsValidJsonBytes() {
        FakturaStatus status = new FakturaStatus();

        assertThat(new String(result, java.nio.charset.StandardCharsets.UTF_8)).contains("fakturaReferanseNr");

    @Test
    void serialize_isConsistentWithObjektMaps() {
        FakturaStatus status = new FakturaStatus();
        status.setFakturaReferanseNr("REF-002");
        status.setStatus("FEIL");

        assertThat(new String(serialized, java.nio.charset.StandardCharsets.UTF_8)).isEqualTo(fromObjektMaps);
    }
}

