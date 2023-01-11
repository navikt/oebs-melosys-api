package no.nav.oebs.melosys.health;

import no.nav.oebs.melosys.db.repository.KallLoggRepository;
import org.springframework.stereotype.Component;

/**
 * Helsesjekk som brukes for å sjekke at databasen er tilgjengelig for applikasjonen.
 */
@Component
public class HealthCheckDbProbe {

	private KallLoggRepository kallLoggRepository;

	HealthCheckDbProbe(KallLoggRepository kallLoggRepository) {
		this.kallLoggRepository = kallLoggRepository;
	}

	/**
	 * Pinger databasen ved å forsøke en spørring mot kall-loggen, men henter ingen data.
	 */
	public void pingDatabase() {
		kallLoggRepository.pingKallLogg();
	}
}
