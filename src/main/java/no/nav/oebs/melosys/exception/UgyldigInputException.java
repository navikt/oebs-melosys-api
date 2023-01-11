package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes dersom PL/SQL-prosedyren har funnet at det er feil i requestdataene.
 */
public class UgyldigInputException extends PlsqlException {

	private static final long serialVersionUID = 1L;

	public UgyldigInputException(String message) {
		super(message);
	}
}
