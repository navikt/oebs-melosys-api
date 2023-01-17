package no.nav.oebs.melosys.kafka;

import java.util.Map;

import no.nav.oebs.melosys.db.entity.Faktura;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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

    //@Value("${spring.kafka.consumer.bootstrap-servers}")
    //private String bootstrapServer;


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Faktura> kafkaLivshendelseListenerContainerFactory(
            KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, Faktura> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaLivshendelseConsumerFactory(properties));
        factory.getContainerProperties().setAckMode(AckMode.RECORD);

        return factory;
    }

    private ConsumerFactory<String, Faktura> kafkaLivshendelseConsumerFactory(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        consumerProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProperties,
                new StringDeserializer(),
                new JsonDeserializer<>(Faktura.class));
    }

}
