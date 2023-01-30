package no.nav.oebs.melosys.kafka;

import java.time.Duration;
import java.util.Map;

import no.nav.oebs.melosys.db.entity.Faktura;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.LogIfLevelEnabled;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@EnableKafka
@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class KafkaConfig {

    // Max antall forsøk på retry når feil kastes tilbake til Spring Kafka fra applikasjonens Kafka-listener.
    @Value("${app.kafka.retry-max-attempts}")
    private int retryMaxAttempts;

    // Antall millisekunder mellom hver retry.
    @Value("${app.kafka.retry-backoff-period-ms}")
    private long retryBackoffPeriod;

    // Antall sekunder mellom hver gang Spring gjør retry ved AuthorizationException fra Kafka.
    @Value("${app.kafka.authorization-exception-retry-interval-secs}")
    private long authorizationExceptionRetryIntervalSecs;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory(properties));
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true); // sync or Async
        factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.INFO); // log commits av offset
        // retries og backoff for commits
        factory.getContainerProperties().setCommitRetries(retryMaxAttempts);
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofMillis(retryBackoffPeriod));
        return factory;
    }

    private ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        return new DefaultKafkaConsumerFactory<>(consumerProps(properties));
    }

    private Map<String, Object> consumerProps(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return  consumerProperties;
    }

    private Map<String, Object> producerProps(KafkaProperties properties){
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        return producerProperties;
    }
}
