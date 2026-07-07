package no.nav.oebs.melosys.kafka;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.json.JsonMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class FakturaConsumer {

    private static final String PLSQL_PROCEDURE = "apps.xxrtv_ar_melosys_pkg.fakturaimport";

    private final PlsqlProcedureRepository plsqlProcedureRepository;
    private final StatusFakturaProducerService fakturaStatusProducerService;

    public FakturaConsumer(PlsqlProcedureRepository plsqlProcedureRepository,
                           StatusFakturaProducerService fakturaStatusProducerService) {
        this.plsqlProcedureRepository = plsqlProcedureRepository;
        this.fakturaStatusProducerService = fakturaStatusProducerService;
    }

    private static final JsonMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
            .build();



    @KafkaListener(topics = "${app.kafka.topics.test-faktura}",
            groupId = "${spring.kafka.consumer.group-id}", errorHandler = "kafkaErrorHandler")
    public void consumeMessages(ConsumerRecord<String, String> consumerRecord, Acknowledgment acks) throws Exception {
        long startTime = System.currentTimeMillis();
        String kafkaPosition = " partition: " + consumerRecord.partition() + ", offset: " + consumerRecord.offset() + ", message: ";
        log.info("Message consumed from topic {} with offset {}",  consumerRecord.topic(), consumerRecord.offset());
        long endTime = System.currentTimeMillis();
        plsqlProcedureRepository.saveKallLogg(kallLoggBuilder(PLSQL_PROCEDURE, consumerRecord.value(), endTime - startTime, null,null, kafkaPosition ));

        PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, consumerRecord.value());
        if (result.getMessageNumber() == PlsqlMessageCodes.OK) {
            acks.acknowledge();
            log.info("Message consumed from partition and topic: {},{}", consumerRecord.partition(), consumerRecord.offset());
        } else if (result.getMessageNumber() == PlsqlMessageCodes.EXCEPTION) {
            Faktura faktura = mapFaktura(consumerRecord.value());
            FakturaStatusFeilImport fakturaStatus = new FakturaStatusFeilImport(faktura.getFakturaReferanseNr(), result.getMessage());
            fakturaStatusProducerService.sendFakturaStatusVedFeil(fakturaStatus);
            acks.acknowledge();
            log.info("Offset commited but message is not processed by OeBS due to errormessage: {} \n caused by invalid input {}",
                    result.getMessage(), LoggingUtils.maskIfFnr(consumerRecord.value()));
        } else {
            Exception ex = new RuntimeException("Unknown exception occurred while processing message at topic: "
                    + consumerRecord.topic() + ", partition: " + consumerRecord.partition()
                    + ", offset: " + consumerRecord.offset()
                    + " — result message: " + result.getMessage());
            acks.acknowledge();
            throw ex;
        }

    }
    private KallLogg kallLoggBuilder(String procedureName, String dataIn, long executionTime, PlsqlProcedureResult result, Exception exception, String kafkaPosition){
        return KallLogg.builder()
                .korrelasjonId(plsqlProcedureRepository.generateAndSetCorrelationId())
                .tidspunkt(LocalDateTime.now(ZoneId.systemDefault()))
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
    }

    private Faktura mapFaktura(String json) {
        try {
            return objectMapper.readValue(json, Faktura.class);
        } catch (JacksonException e) {
            throw new SerializationException(e);
        }
    }


}
