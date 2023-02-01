package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.api.common.utils.ObjektMaps;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.FakturaTest;
import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static no.nav.oebs.melosys.config.common.mdc.MdcOperations.generateCorrelationId;

@Slf4j
@Component
public class FakturaConsumer {

    private static final String PLSQL_PROCEDURE = "prosedyrenavn_inn";

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;

    // TODO: PSLQL exception håndtering
    // TODO: KafkaListener exception håndtering
    @KafkaListener(topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessages(ConsumerRecord<String, FakturaTest> record, Acknowledgment acks) {
        long startTime = System.currentTimeMillis();
        System.out.println("LESER FRA KAFKA TOPIC...");
        log.info("Melding hentet fra partition: {} med offset {} fra topic {}", record.partition(), record.offset(), record.topic());
        //FakturaTest fakturaTest = objektMaps.toObject(record.value(), FakturaTest.class);
        //log.info("FAKTURA KLASSE: {}", fakturaTest.getClass() );
        long endTime = System.currentTimeMillis();

        log.info(KallLoggBuilder(PLSQL_PROCEDURE, record.value().toString(), endTime - startTime, null).toString());

        //plsqlProcedureRepository.saveKallLogg(KallLoggBuilder(PLSQL_PROCEDURE, record.value(), endTime - startTime, null ));


        //PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, fakturaJson);
//        if (result.getMessageNumber() == 0)
//            //acks.acknowledge();
//            log.info("Committing offset: {}", record.offset());
//        else{
//            //håndtere error før commit
//        }

    }
    private KallLogg KallLoggBuilder(String procedureName, String dataIn, long executionTime, Exception exception){
        KallLogg kallLogg = KallLogg.builder()
                .korrelasjonId(generateCorrelationId())
                .tidspunkt(LocalDateTime.now())
                .type(KallLogg.TYPE_KAFKA)
                .kallRetning(KallLogg.RETNING_INN)
                .operation(procedureName)
                .status(exception != null ? Integer.valueOf(PlsqlMessageCodes.EXCEPTION)
                        : Integer.valueOf(PlsqlMessageCodes.OK)) // PlsqlProcedureResult.getMessageNumber(result)
                .kalltid(executionTime)
                .request(dataIn)
                .response(null) // result != null ? result.getData() : null
                .logginfo(exception != null
                                    ? LoggingUtils.formatExceptionAsString(exception)
                        : "Placeholder") //PlsqlProcedureResult.getMessage(result)
                .build();
        return kallLogg;
    }



}
