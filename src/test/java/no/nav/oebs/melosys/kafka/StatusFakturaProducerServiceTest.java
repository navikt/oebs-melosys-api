package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import no.nav.oebs.melosys.exception.InputValidationException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusFakturaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, FakturaStatus> kafkaTemplate;

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private StatusFakturaProducerService service;

    private static final String TOPIC = "faktura-status-topic";
    private static final String FAKTURA_STATUS_JSON = "{\"fakturaReferanseNr\":\"REF-001\",\"status\":\"SENDT\"}";

    @BeforeEach
    void setUp() {
        service = new StatusFakturaProducerService(
                kafkaTemplate,
                plsqlProcedureRepository,
                JsonMapper.builder().findAndAddModules().build());
        ReflectionTestUtils.setField(service, "topic", TOPIC);
    }

    @Test
    void sendFakturaStatus_success_sendsToKafkaAndSavesKallLogg() {
        FakturaStatus fakturaStatus = new FakturaStatus();
        fakturaStatus.setFakturaReferanseNr("REF-001");

        @SuppressWarnings("unchecked")
        SendResult<String, FakturaStatus> sendResult = mock(SendResult.class);
        when(sendResult.getProducerRecord()).thenReturn(new ProducerRecord<>(TOPIC, fakturaStatus));

        when(kafkaTemplate.send(anyString(), any(FakturaStatus.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        PlsqlProcedureResult plsqlResult = new PlsqlProcedureResult((String) null, 0, "OK");

        assertDoesNotThrow(() -> service.sendFakturaStatus(fakturaStatus, plsqlResult, "FAKTURA.PROSEDYRE"));

        verify(kafkaTemplate).send(TOPIC, fakturaStatus);
        verify(plsqlProcedureRepository).saveKallLogg(any());
    }

    @Test
    void sendFakturaStatus_kafkaException_throwsRuntimeException() {
        FakturaStatus fakturaStatus = new FakturaStatus();
        fakturaStatus.setFakturaReferanseNr("REF-001");

        when(kafkaTemplate.send(anyString(), any(FakturaStatus.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka er nede")));

        assertThrows(RuntimeException.class, () ->
                service.sendFakturaStatus(fakturaStatus, null, "FAKTURA.PROSEDYRE"));
    }

    @Test
    void sendFakturaStatus_nullStatus_throwsInputValidationException() {
        assertThrows(InputValidationException.class, () ->
                service.sendFakturaStatus(null, null, "FAKTURA.PROSEDYRE"));
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void sendFakturaStatusVedFeil_delegatesToSendFakturaStatus() {
        FakturaStatusFeilImport feilStatus = new FakturaStatusFeilImport("REF-001", "Noe gikk galt");

        @SuppressWarnings("unchecked")
        SendResult<String, FakturaStatus> sendResult = mock(SendResult.class);
        when(sendResult.getProducerRecord()).thenReturn(new ProducerRecord<>(TOPIC, feilStatus));

        when(kafkaTemplate.send(anyString(), any(FakturaStatus.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        assertDoesNotThrow(() -> service.sendFakturaStatusVedFeil(feilStatus));

        verify(kafkaTemplate).send(TOPIC, feilStatus);
    }

    @Test
    void hentOgSplitFakturaStatus_withNullData_doesNotSendKafkaMessages() {
        when(plsqlProcedureRepository.executeOutProcedure(anyString()))
                .thenReturn(new PlsqlProcedureResult((String) null, 0, "OK"));

        service.hentOgSplitFakturaStatus();

        verify(kafkaTemplate, never()).send(anyString(), any(FakturaStatus.class));
    }

    @Test
    void hentOgSplitFakturaStatus_withEmptyData_doesNotSendKafkaMessages() {
        when(plsqlProcedureRepository.executeOutProcedure(anyString()))
                .thenReturn(new PlsqlProcedureResult("", 0, "OK"));

        service.hentOgSplitFakturaStatus();

        verify(kafkaTemplate, never()).send(anyString(), any(FakturaStatus.class));
    }

    @Test
    void hentOgSplitFakturaStatus_withOneLineOfData_sendsOneFakturaStatusMessage() {
        when(plsqlProcedureRepository.executeOutProcedure(anyString()))
                .thenReturn(new PlsqlProcedureResult(FAKTURA_STATUS_JSON, 0, "OK"));

        SendResult<String, FakturaStatus> sendResult = mock(SendResult.class);
        when(sendResult.getProducerRecord()).thenReturn(new ProducerRecord<>(TOPIC, new FakturaStatus()));
        when(kafkaTemplate.send(anyString(), any(FakturaStatus.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        service.hentOgSplitFakturaStatus();

        FakturaStatus expected = new FakturaStatus();
        expected.setFakturaReferanseNr("REF-001");
        expected.setStatus("SENDT");
        verify(kafkaTemplate, times(1)).send(TOPIC, expected);
    }
}
