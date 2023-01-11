package no.nav.oebs.melosys.config;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Konfigurasjonsklasse for Jackson ObjectMapper.
 */
@Configuration
public class JacksonConfig {

	/**
	 * Tilpasser ObjectMapper-instansen som Spring oppretter til behovet i applikasjonen. Tilpasningen kommer i tillegg til
	 * autokonfigurasjonen, ikke istedet for.
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
		return builder -> builder.featuresToDisable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY) //
				.timeZone(TimeZone.getDefault()); // Bruk plattform default som default, ikke UTC.
	}
}
