package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
import no.nav.oebs.melosys.db.entity.Faktura;
import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class FakturaConsumer {

    private static final String PLSQL_PROCEDURE = "xxrtv_ar_melosys_pkg.fakturaimport";

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;

    @Autowired
    private StatusFakturaProducerService fakturaStatusProducerService;

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature())
            .build();



    @KafkaListener(topics = "${app.kafka.topics.testFaktura}",
            groupId = "${spring.kafka.consumer.group-id}", errorHandler = "kafkaErrorHandler")
    public void consumeMessages(ConsumerRecord<String, String> record, Acknowledgment acks) throws Exception {
        long startTime = System.currentTimeMillis();
        String kafkaPosition = " partition: " + record.partition() + ", offset: " + record.offset() + ", message: ";
        System.out.println("LESER FRA KAFKA TOPIC...");
        log.info("Melding hentet fra partition: {} med offset {} fra topic {}", record.partition(), record.offset(), record.topic());
        long endTime = System.currentTimeMillis();
        plsqlProcedureRepository.saveKallLogg(KallLoggBuilder(PLSQL_PROCEDURE, record.value(), endTime - startTime, null,null, kafkaPosition ));

        PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, record.value());
        if (result.getMessageNumber() == PlsqlMessageCodes.OK) {
            acks.acknowledge();
            log.info("Committing partition and offset: {},{}", record.partition(), record.offset());
        } else if (result.getMessageNumber() == PlsqlMessageCodes.EXCEPTION) {
            Faktura faktura = mapFaktura(record.value());
            FakturaStatusFeilImport fakturaStatus = new FakturaStatusFeilImport(faktura.getFakturaReferanseNr(), result.getMessage());
            fakturaStatusProducerService.sendFakturaStatusVedFeil(fakturaStatus);
            acks.acknowledge();
            log.info("Error in input: {}, errormessage: {}", record.value(), result.getMessage());
            log.info("Committing partition and offset: {},{}", record.partition(), record.offset());
        } else {
            Exception ex = new RuntimeException("Ukjent feil oppstått ved lagring til databasen");
            acks.acknowledge();
            throw ex;
        }

    }
    private KallLogg KallLoggBuilder(String procedureName, String dataIn, long executionTime, PlsqlProcedureResult result, Exception exception, String kafkaPosition){
        KallLogg kallLogg = KallLogg.builder()
                .korrelasjonId(plsqlProcedureRepository.generateAndSetCorrelationId())
                .tidspunkt(LocalDateTime.now())
                .type(KallLogg.TYPE_KAFKA)
                .kallRetning(KallLogg.RETNING_INN)
                .operation(procedureName)
                .status(exception != null ? Integer.valueOf(PlsqlMessageCodes.EXCEPTION)
                        : Integer.valueOf(PlsqlMessageCodes.OK)) // PlsqlProcedureResult.getMessageNumber(result)
                .kalltid(executionTime)
                .request(LoggingUtils.maskIfFnr(dataIn))
                .response(result != null ? result.getData() : null)
                .logginfo(exception != null
                        ? kafkaPosition + LoggingUtils.formatExceptionAsString(exception)
                        : kafkaPosition + PlsqlProcedureResult.getMessage(result))
                .build();
        return kallLogg;
    }

    private Faktura mapFaktura(String json) {
        try {
            return objectMapper.readValue(json, Faktura.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }


}
