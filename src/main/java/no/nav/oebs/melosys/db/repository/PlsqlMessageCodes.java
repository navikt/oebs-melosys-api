package no.nav.oebs.melosys.db.repository;

/**
 * Meldingskoder som returneres fra PL/SQL-prosedyrer.
 */
public class PlsqlMessageCodes {

	private PlsqlMessageCodes() {
	}

	public static final int OK = 0;
	public static final int EXCEPTION = -1;
	public static final int FEIL_I_INPUT = -20882;

	// -9 jobber med den
	// ha med jobb kall id i kall til pakke

	// legge inn først så kalle
	// eller begge samtidig

	// ett felt hodenivå og linjenivå kall ID er fremmednøkkel
}
