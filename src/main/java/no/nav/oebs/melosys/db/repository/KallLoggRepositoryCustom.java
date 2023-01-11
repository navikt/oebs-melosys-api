package no.nav.oebs.melosys.db.repository;

/**
 * Egendefinerte metoder for KallLogg-repository.
 */
public interface KallLoggRepositoryCustom {

	/**
	 * Kjører en select mot tabellen KallLogg, uten å finne noen rader. Vil feile hvis databasen ikke er tilgjengelig.
	 */
	void pingKallLogg();
}
