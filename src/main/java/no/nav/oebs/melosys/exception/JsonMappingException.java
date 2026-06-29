package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes dersom det oppstår en feil under JSON-mapping.
 */
public class JsonMappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonMappingException(Exception cause) {
		super(cause);
	}

}
