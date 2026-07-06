package no.nav.oebs.melosys.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomLocalDateDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private CustomLocalDateDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new CustomLocalDateDeserializer(LocalDate.class);
    }

    @Test
    void deserialize_withValidDdMmYyyyFormat_returnsLocalDate(){
        when(jsonParser.getString()).thenReturn("30.06.2026");

        LocalDate result = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(LocalDate.of(2026, Month.JUNE, 30), result);
    }

    @Test
    void deserialize_withIsoFormat_throwsRuntimeException() {
        when(jsonParser.getString()).thenReturn("2026-06-30");

        assertThrows(RuntimeException.class, () ->
                deserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    void deserialize_withEmptyString_throwsRuntimeException() {
        when(jsonParser.getString()).thenReturn("");

        assertThrows(RuntimeException.class, () ->
                deserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    void deserialize_withInvalidText_throwsRuntimeException() {
        when(jsonParser.getString()).thenReturn("ikke-en-dato");

        assertThrows(RuntimeException.class, () ->
                deserializer.deserialize(jsonParser, deserializationContext));
    }
}
