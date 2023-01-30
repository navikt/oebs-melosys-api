package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakturaConsumer {

    private static final String PLSQL_PROCEDURE = "prosedyrenavn";

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;



    // TODO: PSLQL exception håndtering
    // TODO: KafkaListener exception håndtering
    @KafkaListener(topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessages(ConsumerRecord<String, String> record, Acknowledgment acks) {
        // håndtere data her
        System.out.println("LESER FRA KAFKA TOPIC...");
        log.info("Melding fra kafka topic: {}", record);
        String fakturaJson = record.value();
        log.info("Melding hentet fra partition: {} med offset {}", record.partition(), record.offset());
        log.info("Json i String format: {}", fakturaJson);
        acks.acknowledge();
        //PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, fakturaJson);
//        if (result.getMessageNumber() == 0)
//            //acks.acknowledge();
//            log.info("Committing offset: {}", record.offset());
//        else{
//            //håndtere error før commit
//        }

    }



}
