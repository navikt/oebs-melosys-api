package no.nav.oebs.melosys.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

/**
 * Egen Kafka-producer for å sende RÅ JSON-string til test-topics
 */
@Configuration
public class TestFakturaRawProducerConfig {

    @Bean("testFakturaRawProducerFactory")
    public ProducerFactory<String, String> testFakturaRawProducerFactory(KafkaProperties props) {
        var cfg = new HashMap<>(props.buildProducerProperties(null));
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean("testFakturaRawKafkaTemplate")
    public KafkaTemplate<String, String> testFakturaRawKafkaTemplate(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            ProducerFactory<String, String> testFakturaRawProducerFactory) {

        return new KafkaTemplate<>(testFakturaRawProducerFactory);
    }
}
