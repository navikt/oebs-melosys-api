package no.nav.oebs.melosys.api;

import lombok.RequiredArgsConstructor;
import no.nav.oebs.melosys.kafka.TestFakturaProducer;
import no.nav.oebs.melosys.db.entity.Faktura;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endepunkter for å sende testmeldinger til Kafka.
 * Kun aktivt i Q2-miljø.
 */
@RestController
@RequestMapping("/internal/kafka/test-faktura")
@RequiredArgsConstructor
@Profile("q2")
public class TestFakturaController {

    private final TestFakturaProducer producer;

    /** Ta inn rå JSON (String) og send til Kafka */
    @PostMapping("/raw")
    public ResponseEntity<?> publishRaw(
            @RequestParam(value = "key", required = false) String key,
            @RequestBody String rawJson
    ) {
        producer.validateJsonOrThrow(rawJson);
        producer.sendRaw(key, rawJson);
        return ResponseEntity.accepted().build();
    }

    /** Ta inn DTO og send som JSON-string til Kafka */
    @PostMapping
    public ResponseEntity<?> publishDto(
            @RequestParam(value = "key", required = false) String key,
            @RequestBody Faktura payload
    ) {
        producer.send(payload, key);
        return ResponseEntity.accepted().build();
    }
}

