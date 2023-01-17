package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.api.fakturaimport.FakturaService;
import no.nav.oebs.melosys.db.entity.Faktura;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;

/**
 * Kafka-listener som mottar livshendelser fra topicen.
 */

@Slf4j
@Component
public class FakturaConsumer {

    @Autowired
    private FakturaService fakturaService;
    @KafkaListener(topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            errorHandler = "fakturaErrorHandler")
    public void consumeMessages(ConsumerRecord<String, Faktura> record) {
        // håndtere data her
        log.info("Melding fra kafka topic: {}", record);
        String key = record.key();
        Faktura faktura = record.value();
        // save to database
        //fakturaService.save(faktura);
    }

    private void handleDeserializationException(DeserializationException exception, ConsumerRecord<String, String> consumer) {
        log.error("Feil under deserializering av meldingen: {}", exception);
    }

}
