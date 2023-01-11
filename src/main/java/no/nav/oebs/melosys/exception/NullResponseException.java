package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes dersom det mottas en null-respons.
 */
public class NullResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NullResponseException(String message) {
		super(message);
	}
}
