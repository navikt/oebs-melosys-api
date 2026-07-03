package no.nav.oebs.melosys.config;

import java.util.TimeZone;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;


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
	public JsonMapperBuilderCustomizer jacksonCustomizer() {
		return jsonMapperBuilder -> jsonMapperBuilder
				.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
				.defaultTimeZone(TimeZone.getDefault());
	}
}
