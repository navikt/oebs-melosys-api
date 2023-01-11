package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes ved feil i inputparametere.
 */
public class InputValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InputValidationException(String message) {
		super(message);
	}
}
