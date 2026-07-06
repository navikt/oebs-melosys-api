package no.nav.oebs.melosys.kafka;

import java.util.HashMap;
import java.util.Map;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.exception.InputValidationException;
import no.nav.oebs.melosys.exception.JsonMappingException;
import no.nav.oebs.melosys.exception.UgyldigInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.LogIfLevelEnabled;
import tools.jackson.databind.exc.UnrecognizedPropertyException;


@EnableKafka
@Configuration
public class KafkaConfig {

    // Max antall forsøk på retry når feil kastes tilbake til Spring Kafka fra applikasjonens Kafka-listener.
    @Value("${app.kafka.retry-max-attempts}")
    private int retryMaxAttempts;

    // Antall millisekunder mellom hver retry.
    @Value("${app.kafka.retry-backoff-period-ms}")
    private long retryBackoffPeriod;

    @Value("${app.kafka.retry-interval-max-length}")
    private long retryIntervalMaxLength;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory(properties));
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.INFO); // log commits av offset

        // retries og backoff for commits
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        ExponentialBackOffWithMaxRetries backOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(retryMaxAttempts);
        backOffWithMaxRetries.setInitialInterval(retryBackoffPeriod); // 30 sekunder
        backOffWithMaxRetries.setMultiplier(2);
        backOffWithMaxRetries.setMaxInterval(retryIntervalMaxLength); // 10 minutter
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOffWithMaxRetries);
        errorHandler.addNotRetryableExceptions(UgyldigInputException.class, JsonMappingException.class, InputValidationException.class, org.apache.kafka.common.errors.SerializationException.class, UnrecognizedPropertyException.class);
        errorHandler.setAckAfterHandle(true);
        errorHandler.setCommitRecovered(true);
        return errorHandler;
    }

    private ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        return new DefaultKafkaConsumerFactory<>(consumerProps(properties));
    }

    private Map<String, Object> consumerProps(KafkaProperties properties) {
        return properties.buildConsumerProperties();
    }
    @Bean
    public ProducerFactory<String, FakturaStatus> producerFactory(
            KafkaProperties properties,
            FakturaStatusSerializer fakturaStatusSerializer) {
        DefaultKafkaProducerFactory<String, FakturaStatus> producerFactory =
                new DefaultKafkaProducerFactory<>(producerProps(properties));
        producerFactory.setValueSerializer(fakturaStatusSerializer);
        return producerFactory;
    }

    private Map<String, Object> producerProps(KafkaProperties properties){
        return new HashMap<>(properties.buildProducerProperties());
    }

    @Bean(name = "fakturaStatusTemplate")
    public KafkaTemplate<String, FakturaStatus> kafkaTemplate(
            KafkaProperties properties,
            FakturaStatusSerializer fakturaStatusSerializer) {
        return new KafkaTemplate<>(producerFactory(properties, fakturaStatusSerializer));
    }

}
