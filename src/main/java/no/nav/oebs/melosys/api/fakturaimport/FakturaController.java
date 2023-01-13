package no.nav.oebs.melosys.api.fakturaimport;

import io.swagger.v3.oas.annotations.tags.Tag;
import no.nav.oebs.melosys.Application;
import no.nav.oebs.melosys.api.common.swagger.FakturaSwagger;
import no.nav.security.token.support.core.api.Protected;
import no.nav.oebs.melosys.config.SwaggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
*/

/**
 * REST-controller for henting av faktura API.
 */

@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.FAKTURA, description = "Faktura API")
public class FakturaController {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private final FakturaService service;

	public FakturaController(FakturaService service) { //,
			this.service = service;
	}

	/**
	 * Finner fakturaer som består av en liste med transaksjoner
	 *
	 */

	@Protected
	@GetMapping(path = "/fakturaimport")
	@FakturaSwagger
	public String finnFaktura() {
		logger.info("Kaller get metode");
		return "Hello World";
		// return service.finnFaktura();
	}
}
