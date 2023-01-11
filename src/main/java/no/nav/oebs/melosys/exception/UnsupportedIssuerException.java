package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes dersom det mottas et aksesstoken med ukjent issuer-verdi.
 */
public class UnsupportedIssuerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedIssuerException(String message) {
		super(message);
	}
}
