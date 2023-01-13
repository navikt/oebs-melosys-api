package no.nav.oebs.melosys.config;

import org.springframework.context.annotation.Configuration;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

/**
 * Konfigurasjonsklasse for token-basert sikkerhet.
 */
@Configuration
//@EnableJwtTokenValidation(ignore = { "org.springframework", "org.springdoc" })
public class SecurityConfig {

}