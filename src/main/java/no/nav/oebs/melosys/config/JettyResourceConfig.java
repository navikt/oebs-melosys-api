package no.nav.oebs.melosys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyResourceFactory;

/**
 * Konfigurasjonsklasse for Jetty-ressurser.
 */
@Configuration
public class JettyResourceConfig {

	/**
	 * Opprettet en factory for å håndtere Jetty-ressurser som en del av livssyklusen til en Spring ApplicationContext.
	 */
	@Bean
	public JettyResourceFactory jettyResourceFactory() {
		return new JettyResourceFactory();
	}
}
