package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class FakturaStatusSerializer implements Serializer<FakturaStatus> {

    private final JsonMapper jsonMapper;

    public FakturaStatusSerializer(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public byte[] serialize(String topic, FakturaStatus data) {
        if (data == null) return new byte[0];
        try {
            return jsonMapper.writeValueAsBytes(data);
        } catch (JacksonException e) {
            throw new SerializationException("Kunne ikke serialisere FakturaStatus til JSON", e);
        }
    }
}
