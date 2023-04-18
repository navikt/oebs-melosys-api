package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.api.common.utils.ObjektMaps;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static no.nav.oebs.melosys.config.common.mdc.MdcOperations.generateCorrelationId;

@Slf4j
@Service
public class StatusFakturaProducerServiceImpl implements StatusFakturaProducerService {

    @Autowired
    @Qualifier("fakturaStatusTemplate")
    private KafkaTemplate<String, FakturaStatus> kafkaTemplate;

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private static final String PLSQL_PROCEDURE = "xxrtv_mel_oebs_ve_v1.xxrtv_mel_faktura_status";

    private ObjektMaps objektMaps = new ObjektMaps(new ObjectMapper());

    @Value("${spring.kafka.producer.topic}")
    private String topic;


    @Override
    public void sendFakturaStatus(FakturaStatus status) {
        Exception exception = null;
        long startTime = System.currentTimeMillis();
        String korrelasjonId = plsqlProcedureRepository.generateAndSetCorrelationId();
        String dataOut = null;
        CompletableFuture<SendResult<String, FakturaStatus>> future = kafkaTemplate.send(topic ,status);
        try {
            SendResult<String, FakturaStatus> sendeResultat = future.get();
            dataOut = objektMaps.toJson(sendeResultat.getProducerRecord().value());
            log.info("Melding sendt til kafka topic: {} \n Melding: {}", topic, dataOut);
            //plsqlResult =
        } catch (InterruptedException e) {
            exception = e;
            Thread.currentThread().interrupt();
            String msg = MessageFormat.format(
                    "Avbrutt ved sending av faktura status med vedtaksId: {0} og fakturaref: {1}",
                    status.getVedtaksId(), status.getFakturaReferanseNr());
            throw new RuntimeException(msg ,e);
        } catch (Exception e) {
            exception = e;
            String msg = MessageFormat.format(
                    "Kunne ikke sende fakurastatus for vedtaksId: {0} og fakturaref: {1}",
                    status.getVedtaksId(), status.getFakturaReferanseNr());
            throw new RuntimeException(e);
        } finally {
            log.info("Lagrer kallLogg");
            log.info("dataOut: {}", dataOut);
            long endTime = System.currentTimeMillis();
            plsqlProcedureRepository.saveKallLogg(
                    KallLoggBuilder(korrelasjonId ,PLSQL_PROCEDURE, dataOut, endTime-startTime, null, exception));
        }

    }

    @Override
    public void hentFakturaStatusOgSend() {
        PlsqlProcedureResult result = plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, "");
        FakturaStatus fakturaStatus = objektMaps.toObject(result.getMessage(), FakturaStatus.class);
        if (result.getMessageNumber() == 0) {
            log.info("Sender fakuraStatus til kafka: {}", fakturaStatus);
            sendFakturaStatus(fakturaStatus);
        } else {
            log.error("Feil ved henting av fakuraStatus fra OeBS: {}, {}", result.getMessageNumber(), result.getMessage());
            // log feilmelding
        }
    }

    private KallLogg KallLoggBuilder(String korrelasjonId, String procedureName, String dataOut, long executionTime, PlsqlProcedureResult result, Exception exception) {
        KallLogg kallLogg = KallLogg.builder()
                .korrelasjonId(korrelasjonId)
                .tidspunkt(LocalDateTime.now())
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
        return kallLogg;
    }

}
