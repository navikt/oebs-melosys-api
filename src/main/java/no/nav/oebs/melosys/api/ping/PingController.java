package no.nav.oebs.melosys.api.ping;

import no.nav.oebs.melosys.health.HealthCheckDbProbe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import no.nav.security.token.support.core.api.Unprotected;

/**
 * REST-controller som tilbyr en ping-operasjon.
 */
 //@RestController
@RequestMapping(path = "/api/v1")
// @Api(tags = { SwaggerConfig.PING_TAG })
public class PingController {

	private HealthCheckDbProbe healthCheckDbProbe;

	public PingController(HealthCheckDbProbe healthCheckDbProbe) {
		this.healthCheckDbProbe = healthCheckDbProbe;
	}

	/**
	 * Sjekker om databasen er tilgjengelig.
	 */
	@Unprotected
	@GetMapping(path = "/ping")
	@PingSwagger
	public void ping() {
		healthCheckDbProbe.pingDatabase();
	}
}
