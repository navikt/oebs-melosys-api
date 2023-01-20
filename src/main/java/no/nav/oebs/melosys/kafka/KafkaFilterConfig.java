package no.nav.oebs.melosys.kafka;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.filter")
@Getter
@Setter
public class KafkaFilterConfig {

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime startTime;

    private List<String> faktura = new ArrayList<>();

    private boolean debugMode;
}
