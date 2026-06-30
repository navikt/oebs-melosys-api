package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.common.utils.ObjektMaps;
import no.nav.oebs.melosys.config.common.logging.LoggingUtils;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Service
public class StatusFakturaProducerServiceImpl implements StatusFakturaProducerService {

    private static final String KAFKA_INN_FEIL = "FEIL VED IMPORT";
    @Autowired
    @Qualifier("fakturaStatusTemplate")
    private KafkaTemplate<String, FakturaStatus> kafkaTemplate;

    @Autowired
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private static final String PLSQL_PROCEDURE = "apps.xxrtv_ar_melosys_pkg.fakturastatus";

    private final ObjektMaps objektMaps = new ObjektMaps(new ObjectMapper());

    @Value("${app.kafka.topics.faktura-status}")
    private String topic;


    @Override
    public void sendFakturaStatus(FakturaStatus status, PlsqlProcedureResult result, String procedureName) {
        Exception exception = null;
        long startTime = System.currentTimeMillis();
        String korrelasjonId = plsqlProcedureRepository.generateAndSetCorrelationId();
        String dataOut = null;
        CompletableFuture<SendResult<String, FakturaStatus>> future = kafkaTemplate.send(topic ,status);
        try {
            SendResult<String, FakturaStatus> sendeResultat = future.get();
            dataOut = objektMaps.toJson(sendeResultat.getProducerRecord().value());
            log.info("Melding sendt til kafka topic: {} \n Melding: {}", topic, dataOut);
        } catch (InterruptedException e) {
            exception = e;
            Thread.currentThread().interrupt();
            String msg = MessageFormat.format(
                    "Avbrutt ved sending av faktura status med fakturaref: {0}",
                     status.getFakturaReferanseNr());
            throw new RuntimeException(msg ,e);
        } catch (Exception e) {
            exception = e;
            String msg = MessageFormat.format(
                    "Kunne ikke sende fakurastatus for fakturaref: {0}",
                    status.getFakturaReferanseNr());
            throw new RuntimeException(msg, e);
        } finally {
            log.info("Lagrer kallLogg");
            log.info("dataOut: {}", dataOut);
            long endTime = System.currentTimeMillis();
            plsqlProcedureRepository.saveKallLogg(
                    KallLoggBuilder(korrelasjonId , procedureName, dataOut, endTime-startTime, result, exception));
        }
    }

    public void sendFakturaStatusVedFeil(FakturaStatusFeilImport fakturaStatus) {
        sendFakturaStatus(fakturaStatus, null, KAFKA_INN_FEIL);
    }

    public void hentOgSplitFakturaStatus(){
        PlsqlProcedureResult result = plsqlProcedureRepository.executeOutProcedure(PLSQL_PROCEDURE);
        log.info("FakturaStatus meldinger: {}", result.getData());
        if(result.getData() != null && !result.getData().isEmpty()) {
            Stream<String> fakturaStatusStream = result.getData().lines();
            List<String> fakturaStatus = fakturaStatusStream.toList();
            int i = 1;
            log.info("Antall fakturaStatus meldinger: {}", fakturaStatus.size());
            for (String s : fakturaStatus) {
                log.info("FakturaStatus nr {}: {} ", i++, s);
                sendFakturaStatus(objektMaps.toObject(s, FakturaStatus.class), result, PLSQL_PROCEDURE);
            }
        }
    }

    private KallLogg KallLoggBuilder(String korrelasjonId, String procedureName, String dataOut, long executionTime, PlsqlProcedureResult result, Exception exception) {
        return KallLogg.builder()
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
    }
}
