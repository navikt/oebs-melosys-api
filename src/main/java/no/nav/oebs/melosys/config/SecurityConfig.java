package no.nav.oebs.melosys.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

/**
 * Konfigurasjonsklasse for token-basert sikkerhet.
 */
@Configuration
@EnableJwtTokenValidation(ignore = { "org.springframework", "org.springdoc" })
@ConditionalOnProperty(prefix = "spring.profiles", name = "active", havingValue = "nais")
public class SecurityConfig {

}