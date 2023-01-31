package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import no.nav.oebs.melosys.db.entity.FakturaTest;

import java.io.IOException;


public class FakturaJsonDeserializer extends JsonDeserializer<FakturaTest> {

    @Override
    public FakturaTest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return null;
    }
}
