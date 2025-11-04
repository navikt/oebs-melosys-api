package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.oebs.melosys.config.TopicsProperties;
import no.nav.oebs.melosys.db.entity.Faktura;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer som publiserer faktura-meldinger som rå JSON-strenger til test-topic.
 */
@Service
@RequiredArgsConstructor
public class TestFakturaProducer {

    @Qualifier("testFakturaRawKafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TopicsProperties topics;
    private final ObjectMapper objectMapper;

    /** Sender allerede-serialisert JSON direkte */
    public void sendRaw(String key, String rawJson) {
        String topic = topics.testFaktura();

        if (key != null && !key.isBlank()) {
            kafkaTemplate.send(topic, key, rawJson);
        } else {
            kafkaTemplate.send(topic, rawJson);
        }
    }

    /** Serialiserer Faktura → JSON og sender rått */
    public void send(Faktura faktura, String key) {
        try {
            String json = objectMapper.writeValueAsString(faktura);
            sendRaw(key, json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Kunne ikke serialisere Faktura til JSON", e);
        }
    }

    /** Valgfri validering av raw JSON */
    public void validateJsonOrThrow(String rawJson) {
        try {
            objectMapper.readTree(rawJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON: " + e.getMessage(), e);
        }
    }
}


