package no.nav.oebs.melosys.common.utils;

import no.nav.oebs.melosys.exception.JsonMappingException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private JsonMapper objectMapper;

	public ObjektMaps(JsonMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Mapper fra Java- til JSON-objekt.
	 */
	public <T> String toJson(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JacksonException e) {
			throw new JsonMappingException(e);
		}
	}

	/**
	 * Mapper fra JSON- til Java-objekt.
	 */
	public <T> T toObject(String json, Class<T> valueType) {
		try {
			return objectMapper.readValue(json, valueType);
		} catch (JacksonException e) {
			throw new JsonMappingException(e);
		}
	}

}
