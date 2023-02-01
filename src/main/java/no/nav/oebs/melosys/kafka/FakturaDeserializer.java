package no.nav.oebs.melosys.kafka;



import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import no.nav.oebs.melosys.db.entity.FakturaTest;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;


public class FakturaDeserializer implements Deserializer<FakturaTest> {

    public static final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature())
            .build();

    @Override
    public FakturaTest deserialize(String toupic, byte[] data) {
        try {
            return objectMapper.readValue(data, FakturaTest.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
