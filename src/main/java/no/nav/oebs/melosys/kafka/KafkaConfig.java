package no.nav.oebs.melosys.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.LogIfLevelEnabled;
import org.springframework.util.backoff.FixedBackOff;


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

    @Autowired
    private KafkaTemplate<String, String> deadLetterTemplate;

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
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(deadLetterTemplate,
                (consumerRecord, exception) -> new TopicPartition(consumerRecord.topic(),0));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer,
                new FixedBackOff(1000, 10));

        errorHandler.addNotRetryableExceptions(no.nav.oebs.melosys.exception.UgyldigInputException.class);
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

    // TODO: DEADLETTER PRODUCER FACTORY
    public ProducerFactory<String, String> deadLetterProducerFactory(){
        return new DefaultKafkaProducerFactory<>(deadLetterProducerProps());
    }
    // TODO: DEADLETTER PRODUCER PROPPERTIES
    private Map<String, Object> deadLetterProducerProps(){
        Map<String, Object> producerProperties = new HashMap<>();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "d26apbl007.test.local:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return producerProperties;
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(deadLetterProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, FakturaStatus> kafkaTemplate(KafkaProperties properties) {
        return new KafkaTemplate<>(producerFactory(properties));
    }

    @Bean public CommonLoggingErrorHandler errorHandler() { return new CommonLoggingErrorHandler(); }
}
