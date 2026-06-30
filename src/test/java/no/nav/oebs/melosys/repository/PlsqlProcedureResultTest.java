package no.nav.oebs.melosys.repository;

import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataRetrievalFailureException;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlsqlProcedureResultTest {

    @Nested
    class StringConstructorTests {

        @Test
        void constructor_setsAllFields() {
            var result = new PlsqlProcedureResult("data", 5, "msg");

            assertEquals("data", result.getData());
            assertEquals(5, result.getMessageNumber());
            assertEquals("msg", result.getMessage());
        }

        @Test
        void constructor_nullMessageNumber_defaultsToOk() {
            var result = new PlsqlProcedureResult("data", null, "msg");

            assertEquals(PlsqlMessageCodes.OK, result.getMessageNumber());
        }

        @Test
        void constructor_nullData_isAllowed() {
            var result = new PlsqlProcedureResult(null, 0, null);

            assertNull(result.getData());
            assertNull(result.getMessage());
        }
    }

    @Nested
    class ClobConstructorTests {

        @Test
        void constructor_withClob_readsDataAndMessageNumber() throws SQLException {
            Clob clob = mock(Clob.class);
            when(clob.length()).thenReturn(4L);
            when(clob.getSubString(1, 4)).thenReturn("data");

            var result = new PlsqlProcedureResult(clob, new BigDecimal("7"), "msg");

            assertEquals("data", result.getData());
            assertEquals(7, result.getMessageNumber());
            assertEquals("msg", result.getMessage());
        }

        @Test
        void constructor_withNullClob_dataIsNull() {
            var result = new PlsqlProcedureResult((Clob) null, new BigDecimal("0"), "msg");

            assertNull(result.getData());
        }

        @Test
        void constructor_withNullMessageNumber_defaultsToOk() throws SQLException {
            Clob clob = mock(Clob.class);
            when(clob.length()).thenReturn(1L);
            when(clob.getSubString(1, 1)).thenReturn("x");

            var result = new PlsqlProcedureResult(clob, null, "msg");

            assertEquals(PlsqlMessageCodes.OK, result.getMessageNumber());
        }

        @Test
        void constructor_whenClobThrowsSqlException_throwsDataRetrievalFailureException() throws SQLException {
            Clob clob = mock(Clob.class);
            when(clob.length()).thenThrow(new SQLException("db error"));

            assertThrows(DataRetrievalFailureException.class,
                    () -> new PlsqlProcedureResult(clob, BigDecimal.ONE, "msg"));
        }
    }

    @Test
    void toString_containsFieldValues() {
        var result = new PlsqlProcedureResult("mydata", 42, "mymsg");

        String str = result.toString();

        assertTrue(str.contains("mydata"));
        assertTrue(str.contains("42"));
        assertTrue(str.contains("mymsg"));
    }
}
