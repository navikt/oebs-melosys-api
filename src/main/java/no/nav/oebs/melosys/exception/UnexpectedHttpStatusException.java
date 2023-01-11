package no.nav.oebs.melosys.exception;

import org.springframework.web.reactive.function.client.WebClientException;

/**
 * Exception som kastes dersom et REST-kall returnerer en uventet HTTP-status.
 */
public class UnexpectedHttpStatusException extends WebClientException {

	private static final long serialVersionUID = 1L;

	public UnexpectedHttpStatusException(String message) {
		super(message);
	}
}
