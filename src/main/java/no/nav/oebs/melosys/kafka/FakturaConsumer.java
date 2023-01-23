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
        log.info("Melding fra kafka topic: {}", record);
        String fakturaJson = record.value();
        log.info("Json i String format: {}", fakturaJson);
        PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, fakturaJson);
        if (result.getMessageNumber() == 0)
            acks.acknowledge();
        else{
            //håndtere error før commit
        }

    }

}
