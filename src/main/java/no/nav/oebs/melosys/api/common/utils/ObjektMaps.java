package no.nav.oebs.melosys.api.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.melosys.db.repository.PlsqlMessageCodes;
import no.nav.oebs.melosys.exception.JsonMappingException;
import no.nav.oebs.melosys.exception.TechnicalPlsqlException;
import no.nav.oebs.melosys.exception.UgyldigInputException;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private ObjectMapper objectMapper;

	protected ObjektMaps() {
	}

	public ObjektMaps(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		objectMapper.findAndRegisterModules();
	}

	/**
	 * Kaster exception iht. feilkoden returnert fra PL/SQL-prosedyren.
	 */
	protected void throwPlsqlException(PlsqlProcedureResult result) {
		switch (result.getMessageNumber()) {
		case PlsqlMessageCodes.FEIL_I_INPUT:
			throw new UgyldigInputException(result.getMessage());
		default:
			throw new TechnicalPlsqlException(result.getMessageNumber(), result.getMessage());
		}
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

	/**
	 * Mapper fra JSON- til Java-objekt der generisk typeinformasjon må brukes under mappingen. Dette gjelder typisk for List-
	 * og Map-objekter.
	 */
	protected <T> T toObject(String json, TypeReference<T> objectTypeRef) {
		try {
			return objectMapper.readValue(json, objectTypeRef);
		} catch (JsonProcessingException e) {
			throw new JsonMappingException(e);
		}
	}
}
