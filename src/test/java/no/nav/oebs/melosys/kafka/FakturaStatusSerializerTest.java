package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.common.utils.ObjektMaps;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class FakturaStatusSerializerTest {

    private FakturaStatusSerializer serializer;
    private ObjektMaps objektMaps;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();
        serializer = new FakturaStatusSerializer(jsonMapper);
        objektMaps = new ObjektMaps(jsonMapper);
    }

    @Test
    void serialize_nullData_returnsEmptyArray() {
        assertThat(serializer.serialize("topic", null)).isEmpty();
    }

    @Test
    void serialize_validFakturaStatus_roundTripForAllefelterUtenDato() {
        FakturaStatus status = new FakturaStatus();
        status.setFakturaReferanseNr("REF-001");
        status.setFakturaNummer("12345");
        status.setStatus("SENDT");
        status.setFakturaBelop(new BigDecimal("1000.00"));

        byte[] result = serializer.serialize("topic", status);
        FakturaStatus deserialized = objektMaps.toObject(new String(result), FakturaStatus.class);

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
        status.setDato(LocalDate.of(2026, Month.JANUARY, 15));

        byte[] result = serializer.serialize("topic", status);

        assertThat(new String(result)).contains("\"dato\":\"15-01-2026\"");
    }

    @Test
    void serialize_emptyFakturaStatus_returnsValidJsonBytes() {
        FakturaStatus status = new FakturaStatus();

        byte[] result = serializer.serialize("topic", status);

        assertThat(result).isNotNull().isNotEmpty();
        assertThat(new String(result)).contains("fakturaReferanseNr");
    }

    @Test
    void serialize_isConsistentWithObjektMaps() {
        FakturaStatus status = new FakturaStatus();
        status.setFakturaReferanseNr("REF-002");
        status.setStatus("FEIL");

        byte[] serialized = serializer.serialize("topic", status);
        String fromObjektMaps = objektMaps.toJson(status);

        assertThat(new String(serialized)).isEqualTo(fromObjektMaps);
    }
}