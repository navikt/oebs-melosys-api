package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes dersom det mottas et ugyldig aksesstoken.
 */
public class InvalidAccessTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidAccessTokenException(Exception cause) {
		super(cause);
	}

	public InvalidAccessTokenException(String message, Exception cause) {
		super(message, cause);
	}
}
