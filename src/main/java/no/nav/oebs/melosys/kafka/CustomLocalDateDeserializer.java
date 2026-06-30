package no.nav.oebs.melosys.kafka;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy[dd-MMM-yy]");

    public CustomLocalDateDeserializer() {
        this(null);
    }

    protected CustomLocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, DateTimeParseException {
            String date = jsonParser.getText();
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                throw new RuntimeException(e);
            }
    }
}
