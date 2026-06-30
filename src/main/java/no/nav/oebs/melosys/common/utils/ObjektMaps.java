package no.nav.oebs.melosys.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.melosys.exception.JsonMappingException;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private ObjectMapper objectMapper;

	public ObjektMaps(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		objectMapper.findAndRegisterModules();
	}

	/**
	 * Mapper fra Java- til JSON-objekt.
	 */
	public <T> String toJson(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new JsonMappingException(e);
		}
	}

	/**
	 * Mapper fra JSON- til Java-objekt.
	 */
	public <T> T toObject(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (JsonProcessingException e) {
			throw new JsonMappingException(e);
		}
	}

}
