package no.nav.oebs.melosys.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Konfigurasjonsklasse for Swagger.
 */
@Configuration
public class SwaggerConfig {
	
	public static final String FAKTURA = "Faktura API";

	public static final String BEARER_TOKEN_AUTH = "BearerToken";

@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(new Info()
						.title("Oebs-Melosys API")
						.description("""
								<p><b>OEBS (NAIS) - VERSJON 0.0.1 (10.01.2023)<b></p>
								<p></p>
								<p>REST API'er som er tilbudt av Oebs.</p>
								<p>Sikkerhet:</p>
								<ul>
								<li>API'et støtter aksesstoken utstedt av Azure AD</li>""")
						.version("0.0.1"))
				.components(new Components()
						.addSecuritySchemes(BEARER_TOKEN_AUTH,
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description(
												"Lim inn aksesstoken utstedt av azure AD uten \"Bearer\" foran."
										))
				);
	}
}
