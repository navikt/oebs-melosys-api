package no.nav.oebs.melosys.kafka;

import java.time.Duration;
import java.util.Map;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.exception.InputValidationException;
import no.nav.oebs.melosys.exception.JsonMappingException;
import no.nav.oebs.melosys.exception.UgyldigInputException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.LogIfLevelEnabled;


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

    @Value("${app.kafka.retry-interval-max-length}")
    private long retryIntervalMaxLength;
    private final String deadLetterRecoveryTopic = "team-oebs.faktura-import-dlq.v1-q2";

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory(properties));
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.INFO); // log commits av offset

        // retries og backoff for commits
        factory.getContainerProperties().setCommitRetries(retryMaxAttempts);
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofMillis(retryBackoffPeriod));
        factory.setCommonErrorHandler(defaultErrorHandler(properties));
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaProperties properties) {
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(new KafkaTemplate<>(deadLetterProducerFactory(properties)),
                (consumerRecord, exception) -> new TopicPartition(deadLetterRecoveryTopic,0));
        ExponentialBackOffWithMaxRetries backOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(retryMaxAttempts);
        backOffWithMaxRetries.setInitialInterval(retryBackoffPeriod); // 30 sekunder
        backOffWithMaxRetries.setMultiplier(2);
        backOffWithMaxRetries.setMaxInterval(retryIntervalMaxLength); // 10 minutter
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer,
                /*new FixedBackOff(1000, 10)*/backOffWithMaxRetries);
        errorHandler.addNotRetryableExceptions(UgyldigInputException.class, JsonMappingException.class, InputValidationException.class, org.apache.kafka.common.errors.SerializationException.class, com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class);
        errorHandler.setAckAfterHandle(true);
        errorHandler.setCommitRecovered(true);
        return errorHandler;
    }

    private ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        return new DefaultKafkaConsumerFactory<>(consumerProps(properties));
    }

    private Map<String, Object> consumerProps(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();

        return  consumerProperties;
    }
    @Bean
    public ProducerFactory<String, FakturaStatus> producerFactory(KafkaProperties properties) {
        return new DefaultKafkaProducerFactory<>(producerProps(properties));
    }

    private Map<String, Object> producerProps(KafkaProperties properties){
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        return producerProperties;
    }

    @Bean(name="deadLetterProducerFactory")
    public ProducerFactory<String, String> deadLetterProducerFactory(KafkaProperties properties){
        return new DefaultKafkaProducerFactory<>(deadLetterProducerProps(properties));
    }

    @Bean(name = "deadLetterProducerProps")
    protected Map<String, Object> deadLetterProducerProps(KafkaProperties properties){
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        return producerProperties;
    }

    @Bean(name = "deadLetterTemplate")
    public KafkaTemplate<String, String> deadLetterKafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(deadLetterProducerFactory(properties));
    }

    @Bean(name = "fakturaStatusTemplate")
    public KafkaTemplate<String, FakturaStatus> kafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(producerFactory(properties));
    }

    @Bean public CommonLoggingErrorHandler errorHandler() { return new CommonLoggingErrorHandler(); }

}
