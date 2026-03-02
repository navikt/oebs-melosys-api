package no.nav.oebs.melosys.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TopicsProperties.class)
public class TopicsConfig {}
