package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration",
                "app.kafka.retry-max-attempts=1",
                "app.kafka.retry-backoff-period-ms=100",
                "app.kafka.retry-interval-max-length=200"
        }
)
@EmbeddedKafka(
        partitions = 1,
        topics = {"test-faktura-topic", "faktura-status-topic"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class FakturaConsumerIntegrationTest {

    private static final String FAKTURA_JSON = "{\"fakturaReferanseNr\":\"REF-001\"}";

    @MockitoBean
    private PlsqlProcedureRepository plsqlProcedureRepository;

    @MockitoSpyBean
    private KafkaErrorHandler kafkaErrorHandler;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaTemplate<String, String> testProducer;

    @BeforeEach
    void setUp() {
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        testProducer = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
        when(plsqlProcedureRepository.generateAndSetCorrelationId()).thenReturn("test-correlation-id");
    }

    @Test
    void whenListenerThrowsRuntimeException_errorHandlerIsInvoked() {
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString()))
                .thenThrow(new RuntimeException("Simulated DB failure"));

        testProducer.send(new ProducerRecord<>("test-faktura-topic", FAKTURA_JSON));

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(kafkaErrorHandler, atLeastOnce()).handleError(any(), any(), any())
                );
    }

    @Test
    void whenListenerSucceeds_errorHandlerIsNotInvoked() throws Exception {
        var okResult = new no.nav.oebs.melosys.db.repository.PlsqlProcedureResult(null, 0, "OK");
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(okResult);

        testProducer.send(new ProducerRecord<>("test-faktura-topic", FAKTURA_JSON));

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(plsqlProcedureRepository, atLeastOnce()).executeInOutProcedure(anyString(), anyString())
                );

        verify(kafkaErrorHandler, never()).handleError(any(), any(), any());
    }
}
