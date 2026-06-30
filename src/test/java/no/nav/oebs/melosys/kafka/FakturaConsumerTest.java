package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakturaConsumerTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    @Mock
    private StatusFakturaProducerService fakturaStatusProducerService;

    @Mock
    private Acknowledgment acks;

    @InjectMocks
    private FakturaConsumer fakturaConsumer;

    private static final String FAKTURA_JSON = "{\"fakturaReferanseNr\":\"REF-001\"}";

    @Test
    void consumeMessages_withOkResult_acknowledgesWithoutSendingFakturaStatus() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", FAKTURA_JSON);
        PlsqlProcedureResult result = new PlsqlProcedureResult((String) null, 0, "OK");
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        assertDoesNotThrow(() -> fakturaConsumer.consumeMessages(record, acks));

        verify(acks).acknowledge();
        verify(fakturaStatusProducerService, never()).sendFakturaStatusVedFeil(any());
    }

    @Test
    void consumeMessages_withExceptionResult_sendsStatusVedFeilAndAcknowledges() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", FAKTURA_JSON);
        PlsqlProcedureResult result = new PlsqlProcedureResult((String) null, -1, "Feil i input");
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        assertDoesNotThrow(() -> fakturaConsumer.consumeMessages(record, acks));

        verify(fakturaStatusProducerService).sendFakturaStatusVedFeil(any(FakturaStatusFeilImport.class));
        verify(acks).acknowledge();
    }

    @Test
    void consumeMessages_withUnknownMessageNumber_acknowledgesAndThrowsRuntimeException() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", FAKTURA_JSON);
        PlsqlProcedureResult result = new PlsqlProcedureResult((String) null, 99, "Ukjent");
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        assertThrows(RuntimeException.class, () -> fakturaConsumer.consumeMessages(record, acks));

        verify(acks).acknowledge();
    }
}
