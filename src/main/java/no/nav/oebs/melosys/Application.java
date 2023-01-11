package no.nav.oebs.melosys;

import no.nav.oebs.melosys.config.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot applikasjonsklasse.
 */
@SpringBootApplication
public class 	Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		Props.setProps();

		SpringApplication.run(Application.class, args);
	}
}