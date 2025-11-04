package no.nav.oebs.melosys.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record TopicsProperties(String fakturaStatus, String testFaktura) {}
