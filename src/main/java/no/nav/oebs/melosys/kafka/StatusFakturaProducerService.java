package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.common.utils.ObjektMaps;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.exception.InputValidationException;
import no.nav.oebs.melosys.exception.KafkaProducerException;
import no.nav.oebs.melosys.exception.KafkaProducerInterruptedException;
import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Service
public class StatusFakturaProducerService {

    private static final String KAFKA_INN_FEIL = "FEIL VED IMPORT";
    private final KafkaTemplate<String, FakturaStatus> kafkaTemplate;
    private final PlsqlProcedureRepository plsqlProcedureRepository;

    private static final String PLSQL_PROCEDURE = "apps.xxrtv_ar_melosys_pkg.fakturastatus";

    private final ObjektMaps objektMaps;

    public StatusFakturaProducerService(
            @Qualifier("fakturaStatusTemplate") KafkaTemplate<String, FakturaStatus> kafkaTemplate,
            PlsqlProcedureRepository plsqlProcedureRepository,
            JsonMapper jsonMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.plsqlProcedureRepository = plsqlProcedureRepository;
        this.objektMaps = new ObjektMaps(jsonMapper);
    }

    @Value("${app.kafka.topics.faktura-status}")
    private String topic;


    public void sendFakturaStatus(FakturaStatus status, PlsqlProcedureResult result, String procedureName) {
        if (status == null) {
            throw new InputValidationException("Fakturastatus is null and not valid as Kafka message.");
        }

        Exception exception = null;
        long startTime = System.currentTimeMillis();
        String korrelasjonId = plsqlProcedureRepository.generateAndSetCorrelationId();
        String dataOut = null;
        CompletableFuture<SendResult<String, FakturaStatus>> future = kafkaTemplate.send(topic ,status);
        String messageIdentifier = String.format("fakturaNr: %s fakturaReferanseNr: %s korrelasjonId: %s",
                status.getFakturaNummer(), status.getFakturaReferanseNr(), korrelasjonId);
        try {
            SendResult<String, FakturaStatus> sendeResultat = future.get();
            dataOut = objektMaps.toJson(sendeResultat.getProducerRecord().value());
            log.info("Fakturastatus message produced to topic with {}", messageIdentifier);
        } catch (InterruptedException e) {
            exception = e;
            Thread.currentThread().interrupt();
            String msg = MessageFormat.format(
                    "Message not sent to Kafka for fakturaref: {0} due to interruption",
                    messageIdentifier);
            throw new KafkaProducerInterruptedException(msg, e);
        } catch (Exception e) {
            exception = e;
            String msg = MessageFormat.format(
                    "Message not sent to Kafka for fakturaref: {0} due to exception",
                    messageIdentifier);
            throw new KafkaProducerException(msg, e);
        } finally {
            log.info("Message logged in OeBS for {}", messageIdentifier);
            long endTime = System.currentTimeMillis();
            plsqlProcedureRepository.saveKallLogg(
                    kallLoggBuilder(korrelasjonId , procedureName, dataOut, endTime-startTime, result, exception));
        }
    }

    public void sendFakturaStatusVedFeil(FakturaStatusFeilImport fakturaStatus) {
        sendFakturaStatus(fakturaStatus, null, KAFKA_INN_FEIL);
    }

    public void hentOgSplitFakturaStatus(){
        PlsqlProcedureResult result = plsqlProcedureRepository.executeOutProcedure(PLSQL_PROCEDURE);
        log.info("Retrive FakturaStatus from OeBS with procedure: {}", PLSQL_PROCEDURE);
        if(result.getData() != null && !result.getData().isEmpty()) {
            Stream<String> fakturaStatusStream = result.getData().lines();
            List<String> fakturaStatus = fakturaStatusStream.toList();
            log.info("Received {} FakturaStatus from OeBS", fakturaStatus.size());
            for (String s : fakturaStatus) {
                sendFakturaStatus(objektMaps.toObject(s, FakturaStatus.class), result, PLSQL_PROCEDURE);
            }
        }
    }

    private KallLogg kallLoggBuilder(String korrelasjonId, String procedureName, String dataOut, long executionTime, PlsqlProcedureResult result, Exception exception) {
        return KallLogg.builder()
                .korrelasjonId(korrelasjonId)
                .tidspunkt(LocalDateTime.now(ZoneId.systemDefault()))
                .type(KallLogg.TYPE_KAFKA)
                .kallRetning(KallLogg.RETNING_UT)
                .operation(procedureName)
                .status(exception != null ? Integer.valueOf(PlsqlMessageCodes.EXCEPTION)
                        : Integer.valueOf(PlsqlMessageCodes.OK)) // PlsqlProcedureResult.getMessageNumber(result)
                .kalltid(executionTime)
                .request(dataOut)
                .response(null)
                .logginfo(exception != null
                        ? LoggingUtils.formatExceptionAsString(exception)
                        : PlsqlProcedureResult.getMessage(result))
                .build();
    }
}
