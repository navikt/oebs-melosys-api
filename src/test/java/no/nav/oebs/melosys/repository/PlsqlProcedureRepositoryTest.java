package no.nav.oebs.melosys.repository;

import no.nav.oebs.melosys.db.entity.KallLogg;
import no.nav.oebs.melosys.db.repository.KallLoggRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlsqlProcedureRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private SimpleJdbcCall simpleJdbcCall;

    @Mock
    private KallLoggRepository kallLoggRepository;

    private PlsqlProcedureRepository repository;

    private static final String VALID_PROCEDURE = "PACKAGE.PROCEDURE";
    private static final String VALID_PROCEDURE_WITH_SCHEMA = "SCHEMA.PACKAGE.PROCEDURE";

    @BeforeEach
    void setUp() {
        repository = new PlsqlProcedureRepository(dataSource, kallLoggRepository);

        ConcurrentMap<String, SimpleJdbcCall> cache = new ConcurrentHashMap<>();
        cache.put(VALID_PROCEDURE, simpleJdbcCall);
        cache.put(VALID_PROCEDURE_WITH_SCHEMA, simpleJdbcCall);
        ReflectionTestUtils.setField(repository, "jdbcCallCache", cache);
    }

    // --- executeInOutProcedure ---

    @Test
    void executeInOutProcedure_withInvalidProcedureNameFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                repository.executeInOutProcedure("INVALIDNAME", "{}"));
    }

    @Test
    void executeInOutProcedure_withMissingDot_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                repository.executeInOutProcedure("PACKAGEPROCEDURE", "{}"));
    }

    @Test
    void executeInOutProcedure_withTooManyTokens_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                repository.executeInOutProcedure("A.B.C.D", "{}"));
    }

    @Test
    void executeInOutProcedure_withValidResult_returnsPlsqlProcedureResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(0));
        outParams.put("msg", "OK");

        when(simpleJdbcCall.execute(any(SqlParameterSource.class))).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeInOutProcedure(VALID_PROCEDURE, "{}");

        assertNotNull(result);
        assertEquals(0, result.getMessageNumber());
        assertEquals("OK", result.getMessage());
    }

    @Test
    void executeInOutProcedure_withNegativeMessageNumber_returnsResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(-1));
        outParams.put("msg", "No data found");

        when(simpleJdbcCall.execute(any(SqlParameterSource.class))).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeInOutProcedure(VALID_PROCEDURE, "{}");

        assertNotNull(result);
        assertEquals(-1, result.getMessageNumber());
        assertEquals("No data found", result.getMessage());
    }

    @Test
    void executeInOutProcedure_withPositiveMessageNumber_returnsResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(1));
        outParams.put("msg", "Success");

        when(simpleJdbcCall.execute(any(SqlParameterSource.class))).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeInOutProcedure(VALID_PROCEDURE, "{}");

        assertNotNull(result);
        assertEquals(1, result.getMessageNumber());
    }

    @Test
    void executeInOutProcedure_withSchemaFormat_returnsResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(0));
        outParams.put("msg", "OK");

        when(simpleJdbcCall.execute(any(SqlParameterSource.class))).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeInOutProcedure(VALID_PROCEDURE_WITH_SCHEMA, "{}");

        assertNotNull(result);
        assertEquals(0, result.getMessageNumber());
    }

    // --- executeOutProcedure ---

    @Test
    void executeOutProcedure_withInvalidProcedureNameFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                repository.executeOutProcedure("INVALIDNAME"));
    }

    @Test
    void executeOutProcedure_withValidResult_returnsPlsqlProcedureResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(0));
        outParams.put("msg", "OK");

        when(simpleJdbcCall.execute()).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeOutProcedure(VALID_PROCEDURE);

        assertNotNull(result);
        assertEquals(0, result.getMessageNumber());
        assertEquals("OK", result.getMessage());
    }

    @Test
    void executeOutProcedure_withSchemaFormat_returnsResult() {
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data_out", null);
        outParams.put("msg_no", new BigDecimal(0));
        outParams.put("msg", "OK");

        when(simpleJdbcCall.execute()).thenReturn(outParams);

        PlsqlProcedureResult result = repository.executeOutProcedure(VALID_PROCEDURE_WITH_SCHEMA);

        assertNotNull(result);
        assertEquals(0, result.getMessageNumber());
    }

    // --- saveKallLogg ---

    @Test
    void saveKallLogg_callsRepository() {
        KallLogg kallLogg = KallLogg.builder().build();

        repository.saveKallLogg(kallLogg);

        verify(kallLoggRepository).save(kallLogg);
    }

    @Test
    void saveKallLogg_whenRepositoryThrows_doesNotPropagateException() {
        KallLogg kallLogg = KallLogg.builder().build();
        doThrow(new RuntimeException("DB error")).when(kallLoggRepository).save(any());

        assertDoesNotThrow(() -> repository.saveKallLogg(kallLogg));
    }

    // --- generateAndSetCorrelationId ---

    @Test
    void generateAndSetCorrelationId_returnsNonNullId() {
        String correlationId = repository.generateAndSetCorrelationId();

        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
    }

    @Test
    void generateAndSetCorrelationId_returnsDifferentIdsOnSubsequentCalls() {
        String first = repository.generateAndSetCorrelationId();
        String second = repository.generateAndSetCorrelationId();

        assertNotEquals(first, second);
    }
}
