package no.nav.oebs.melosys.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Konfigurasjonsproperties for tjenester.
 */
@Configuration
@ConfigurationProperties(prefix = "app.service")
@Getter
@Setter
public class ServiceConfig {

    // Antall minutter forsinkelse før første retryforsøk.
    private int retryAttempt1DelayMins;

    // Antall minutter forsinkelse for etterfølgende retryforsøk.
    private int retryAttemptNDelayMins;

    // Maks antall retryforsøk før meldingen settes til status feilet.
    private int retryMaxAttempts;
}
