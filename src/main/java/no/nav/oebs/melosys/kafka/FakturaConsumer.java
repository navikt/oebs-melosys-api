package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import no.nav.oebs.melosys.exception.PlsqlException;
import no.nav.oebs.melosys.exception.UgyldigInputException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class FakturaConsumer {

    private static final String PLSQL_PROCEDURE = "xxrtv_mel_oebs_ve_v1.xxrtv_mel_faktura_bestilt";

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;

    // TODO: PSLQL exception håndtering
    // TODO: KafkaListener exception håndtering
    @KafkaListener(topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}", errorHandler = "kafkaErrorHandler")
    public void consumeMessages(ConsumerRecord<String, String> record, Acknowledgment acks) throws Exception {
        long startTime = System.currentTimeMillis();
        String korrelasjonId = plsqlProcedureRepository.generateAndSetCorrelationId();
        String kafkaPosition = " partition: " + record.partition() + ", offset: " + record.offset() + ", message: ";
        System.out.println("LESER FRA KAFKA TOPIC...");
        log.info("Melding hentet fra partition: {} med offset {} fra topic {}", record.partition(), record.offset(), record.topic());
        long endTime = System.currentTimeMillis();

        //log.info(KallLoggBuilder(PLSQL_PROCEDURE, record.value().toString(), endTime - startTime, null).toString());
        plsqlProcedureRepository.saveKallLogg(KallLoggBuilder(korrelasjonId, PLSQL_PROCEDURE, record.value(), endTime - startTime, null,null, kafkaPosition ));
        acks.acknowledge();

        PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, record.value());
        if (result.getMessageNumber() == PlsqlMessageCodes.OK) {
            acks.acknowledge();
            log.info("Committing partition and offset: {},{}", record.partition(), record.offset());
        } else if (result.getMessageNumber() == PlsqlMessageCodes.FEIL_I_INPUT) {
            throw new UgyldigInputException("Feil i Json string ved lagring til databasen");
        } else {
            throw new RuntimeException("Ukjent feil oppstått ved lagring til databasen");
        }

    }
    private KallLogg KallLoggBuilder(String korrelasjonId, String procedureName, String dataIn, long executionTime, PlsqlProcedureResult result, Exception exception, String kafkaPosition){
        KallLogg kallLogg = KallLogg.builder()
                .korrelasjonId(korrelasjonId)
                .tidspunkt(LocalDateTime.now())
                .type(KallLogg.TYPE_KAFKA)
                .kallRetning(KallLogg.RETNING_INN)
                .operation(procedureName)
                .status(exception != null ? Integer.valueOf(PlsqlMessageCodes.EXCEPTION)
                        : Integer.valueOf(PlsqlMessageCodes.OK)) // PlsqlProcedureResult.getMessageNumber(result)
                .kalltid(executionTime)
                .request(dataIn)
                .response(result != null ? result.getData() : null)
                .logginfo(exception != null
                        ? kafkaPosition + LoggingUtils.formatExceptionAsString(exception)
                        : kafkaPosition + PlsqlProcedureResult.getMessage(result))
                .build();
        return kallLogg;
    }



}
