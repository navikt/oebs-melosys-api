package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class FakturaStatusSerializer implements Serializer<FakturaStatus> {

    private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    @Override
    public byte[] serialize(String topic, FakturaStatus data) {
        if (data == null) return null;
        try {
            return jsonMapper.writeValueAsBytes(data);
        } catch (JacksonException e) {
            throw new SerializationException("Kunne ikke serialisere FakturaStatus til JSON", e);
        }
    }
}
