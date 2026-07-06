package no.nav.oebs.melosys.kafka;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy[dd-MMM-yy]");

    public CustomLocalDateDeserializer() {
        super(LocalDate.class); // ikke null
    }

    protected CustomLocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws DateTimeParseException {
            String date = jsonParser.getString();
            return LocalDate.parse(date, formatter);
    }
}
