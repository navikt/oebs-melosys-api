package no.nav.oebs.melosys.exception;

import no.nav.oebs.melosys.config.common.logging.LoggingUtils;

/**
 * Superklasse for exceptiontyper som kan kastes for feil fra PL/SQL-prosedyrer.
 * <p>
 * NB! Tekst som ser ut til å inneholde et fødselsnummer maskeres.
 */
public abstract class PlsqlException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PlsqlException(String message) {
		super(LoggingUtils.maskIfFnr(message));
	}
}
